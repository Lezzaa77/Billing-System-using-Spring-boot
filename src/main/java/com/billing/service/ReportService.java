package com.billing.service;

import com.billing.dto.SalesReport;
import com.billing.dto.SalesReport.CategoryRow;
import com.billing.dto.SalesReport.DayRow;
import com.billing.dto.SalesReport.ItemRow;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Builds the general total-sales report with plain SQL (JdbcTemplate). */
@Service
public class ReportService {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private final JdbcTemplate jdbc;

    public ReportService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** {@code from} / {@code to} are inclusive and may be null (= no limit). */
    @Transactional(readOnly = true)
    public SalesReport build(LocalDate from, LocalDate to) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (from != null) {
            where.append(" AND sold_at >= ?");
            params.add(Timestamp.valueOf(from.atStartOfDay()));
        }
        if (to != null) {
            where.append(" AND sold_at < ?");
            params.add(Timestamp.valueOf(to.plusDays(1).atStartOfDay()));
        }
        String w = where.toString();
        Object[] args = params.toArray();

        SalesReport report = new SalesReport();

        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM sales" + w, Integer.class, args);
        Long units = jdbc.queryForObject("SELECT COALESCE(SUM(quantity), 0) FROM sales" + w, Long.class, args);
        BigDecimal revenue = jdbc.queryForObject("SELECT COALESCE(SUM(total), 0) FROM sales" + w, BigDecimal.class, args);

        int saleCount = count == null ? 0 : count;
        BigDecimal totalSales = revenue == null ? BigDecimal.ZERO : revenue;
        report.setSaleCount(saleCount);
        report.setUnitsSold(units == null ? 0L : units);
        report.setTotalSales(totalSales);
        report.setAverageSale(saleCount > 0
                ? totalSales.divide(BigDecimal.valueOf(saleCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);

        report.setByCategory(jdbc.query(
                "SELECT category, COUNT(*), SUM(quantity), SUM(total) AS revenue FROM sales" + w
                        + " GROUP BY category ORDER BY revenue DESC",
                (rs, i) -> new CategoryRow(rs.getString(1), rs.getInt(2), rs.getLong(3), rs.getBigDecimal(4)),
                args));

        report.setTopItems(jdbc.query(
                "SELECT item_code, item_name, SUM(quantity), SUM(total) AS revenue FROM sales" + w
                        + " GROUP BY item_code, item_name ORDER BY revenue DESC LIMIT 10",
                (rs, i) -> new ItemRow(rs.getString(1), rs.getString(2), rs.getLong(3), rs.getBigDecimal(4)),
                args));

        report.setByDay(jdbc.query(
                "SELECT DATE(sold_at) AS sale_day, COUNT(*), SUM(quantity), SUM(total) FROM sales" + w
                        + " GROUP BY DATE(sold_at) ORDER BY sale_day DESC",
                (rs, i) -> new DayRow(rs.getObject(1, LocalDate.class), rs.getInt(2), rs.getLong(3), rs.getBigDecimal(4)),
                args));

        return report;
    }

    /** Text such as "All time" or "01 Oct 2026 to 31 Oct 2026". */
    public static String describePeriod(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return "All time";
        }
        return (from == null ? "Start" : from.format(DAY)) + " to " + (to == null ? "Today" : to.format(DAY));
    }
}

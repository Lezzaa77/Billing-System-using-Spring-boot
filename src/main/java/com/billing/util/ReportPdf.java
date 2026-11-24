package com.billing.util;

import com.billing.dto.SalesReport;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Lays out the sales report as a PDF file (returned as bytes). */
public final class ReportPdf {

    private static final String RULE = "-".repeat(66);
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH);

    private ReportPdf() {
    }

    public static byte[] render(SalesReport r, String period, String userName) {
        SimplePdf pdf = new SimplePdf();
        pdf.line("TOTAL SALES REPORT", true, 16);
        pdf.line("Period:    " + period);
        pdf.line("Generated: " + LocalDateTime.now().format(DATE_TIME) + " by " + userName);
        pdf.blank();

        pdf.line("SUMMARY", true, 11);
        pdf.line(RULE);
        pdf.line(String.format("%-28s %18s", "Total sales (revenue)", money(r.getTotalSales())));
        pdf.line(String.format("%-28s %18d", "Number of sales", r.getSaleCount()));
        pdf.line(String.format("%-28s %18d", "Total units sold", r.getUnitsSold()));
        pdf.line(String.format("%-28s %18s", "Average sale value", money(r.getAverageSale())));
        pdf.blank();

        pdf.line("SALES BY CATEGORY", true, 11);
        pdf.line(String.format("%-30s %8s %8s %16s", "Category", "Sales", "Units", "Revenue"));
        pdf.line(RULE);
        if (r.getByCategory().isEmpty()) pdf.line("No sales in this period.");
        for (SalesReport.CategoryRow row : r.getByCategory()) {
            pdf.line(String.format("%-30s %8d %8d %16s", fit(row.getCategory(), 30), row.getSales(), row.getUnits(), money(row.getRevenue())));
        }
        pdf.blank();

        pdf.line("TOP SELLING ITEMS", true, 11);
        pdf.line(String.format("%-10s %-30s %8s %16s", "Item ID", "Name", "Units", "Revenue"));
        pdf.line(RULE);
        if (r.getTopItems().isEmpty()) pdf.line("No sales in this period.");
        for (SalesReport.ItemRow row : r.getTopItems()) {
            pdf.line(String.format("%-10s %-30s %8d %16s", fit(row.getItemCode(), 10), fit(row.getItemName(), 30), row.getUnits(), money(row.getRevenue())));
        }
        pdf.blank();

        pdf.line("SALES BY DAY", true, 11);
        pdf.line(String.format("%-12s %8s %8s %16s", "Date", "Sales", "Units", "Revenue"));
        pdf.line(RULE);
        if (r.getByDay().isEmpty()) pdf.line("No sales in this period.");
        for (SalesReport.DayRow row : r.getByDay()) {
            pdf.line(String.format("%-12s %8d %8d %16s", row.getDay().format(DAY), row.getSales(), row.getUnits(), money(row.getRevenue())));
        }

        return pdf.toBytes();
    }

    private static String money(BigDecimal value) {
        return String.format(Locale.US, "%,.2f", value == null ? BigDecimal.ZERO : value);
    }

    private static String fit(String s, int width) {
        if (s == null) return "";
        return s.length() <= width ? s : s.substring(0, width - 1) + "~";
    }
}

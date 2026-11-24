package com.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Result of the general sales report. */
public class SalesReport {

    private int saleCount;
    private long unitsSold;
    private BigDecimal totalSales = BigDecimal.ZERO;
    private BigDecimal averageSale = BigDecimal.ZERO;
    private List<CategoryRow> byCategory = new ArrayList<>();
    private List<ItemRow> topItems = new ArrayList<>();
    private List<DayRow> byDay = new ArrayList<>();

    public int getSaleCount() { return saleCount; }
    public void setSaleCount(int saleCount) { this.saleCount = saleCount; }
    public long getUnitsSold() { return unitsSold; }
    public void setUnitsSold(long unitsSold) { this.unitsSold = unitsSold; }
    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }
    public BigDecimal getAverageSale() { return averageSale; }
    public void setAverageSale(BigDecimal averageSale) { this.averageSale = averageSale; }
    public List<CategoryRow> getByCategory() { return byCategory; }
    public void setByCategory(List<CategoryRow> byCategory) { this.byCategory = byCategory; }
    public List<ItemRow> getTopItems() { return topItems; }
    public void setTopItems(List<ItemRow> topItems) { this.topItems = topItems; }
    public List<DayRow> getByDay() { return byDay; }
    public void setByDay(List<DayRow> byDay) { this.byDay = byDay; }

    public static class CategoryRow {
        private final String category;
        private final int sales;
        private final long units;
        private final BigDecimal revenue;

        public CategoryRow(String category, int sales, long units, BigDecimal revenue) {
            this.category = category;
            this.sales = sales;
            this.units = units;
            this.revenue = revenue;
        }

        public String getCategory() { return category; }
        public int getSales() { return sales; }
        public long getUnits() { return units; }
        public BigDecimal getRevenue() { return revenue; }
    }

    public static class ItemRow {
        private final String itemCode;
        private final String itemName;
        private final long units;
        private final BigDecimal revenue;

        public ItemRow(String itemCode, String itemName, long units, BigDecimal revenue) {
            this.itemCode = itemCode;
            this.itemName = itemName;
            this.units = units;
            this.revenue = revenue;
        }

        public String getItemCode() { return itemCode; }
        public String getItemName() { return itemName; }
        public long getUnits() { return units; }
        public BigDecimal getRevenue() { return revenue; }
    }

    public static class DayRow {
        private final LocalDate day;
        private final int sales;
        private final long units;
        private final BigDecimal revenue;

        public DayRow(LocalDate day, int sales, long units, BigDecimal revenue) {
            this.day = day;
            this.sales = sales;
            this.units = units;
            this.revenue = revenue;
        }

        public LocalDate getDay() { return day; }
        public int getSales() { return sales; }
        public long getUnits() { return units; }
        public BigDecimal getRevenue() { return revenue; }
    }
}

package com.example.metering;

import java.math.BigDecimal;

public class TotalByMeter {
    private int meterId;
    private BigDecimal totalValue;

    public int getMeterId() { return meterId; }
    public void setMeterId(int meterId) { this.meterId = meterId; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
}

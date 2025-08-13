package com.example.metering;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

public class MeteringRecord {
    private int meterId;
    private Timestamp intervalStart;
    private Timestamp timestamp;
    private int intervalLength;
    private String streamSuffix;
    private Integer streamNumber;
    private String uom;
    private String quality;
    private String methodFlag;
    private Integer reasonCode;
    private String scaled;
    private BigDecimal value;

    public MeteringRecord() { }

    public int getMeterId() { return meterId; }
    public void setMeterId(int meterId) { this.meterId = meterId; }

    public Timestamp getIntervalStart() { return intervalStart; }
    public void setIntervalStart(Timestamp intervalStart) { this.intervalStart = intervalStart; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public int getIntervalLength() { return intervalLength; }
    public void setIntervalLength(int intervalLength) { this.intervalLength = intervalLength; }

    public String getStreamSuffix() { return streamSuffix; }
    public void setStreamSuffix(String streamSuffix) { this.streamSuffix = streamSuffix; }

    public Integer getStreamNumber() { return streamNumber; }
    public void setStreamNumber(Integer streamNumber) { this.streamNumber = streamNumber; }

    public String getUom() { return uom; }
    public void setUom(String uom) { this.uom = uom; }

    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }

    public String getMethodFlag() { return methodFlag; }
    public void setMethodFlag(String methodFlag) { this.methodFlag = methodFlag; }

    public Integer getReasonCode() { return reasonCode; }
    public void setReasonCode(Integer reasonCode) { this.reasonCode = reasonCode; }

    public String getScaled() { return scaled; }
    public void setScaled(String scaled) { this.scaled = scaled; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeteringRecord)) return false;
        MeteringRecord that = (MeteringRecord) o;
        return meterId == that.meterId &&
                intervalLength == that.intervalLength &&
                Objects.equals(intervalStart, that.intervalStart) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(streamSuffix, that.streamSuffix) &&
                Objects.equals(streamNumber, that.streamNumber) &&
                Objects.equals(uom, that.uom) &&
                Objects.equals(quality, that.quality) &&
                Objects.equals(methodFlag, that.methodFlag) &&
                Objects.equals(reasonCode, that.reasonCode) &&
                Objects.equals(scaled, that.scaled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meterId, intervalStart, timestamp, intervalLength, streamSuffix, streamNumber, uom, quality, methodFlag, reasonCode, scaled);
    }
}

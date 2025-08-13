package com.example.metering;

import java.util.List;

public interface MeteringDao {
    int updateIncrementValue(MeteringRecord r);
    int insert(MeteringRecord r);
    void batchUpsert(List<MeteringRecord> records);

    // totals by meter_id only
    int updateIncrementTotalByMeter(TotalByMeter t);
    int insertTotalByMeter(TotalByMeter t);
    void batchUpsertTotalsByMeter(List<TotalByMeter> totals);
}

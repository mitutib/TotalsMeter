package com.example.metering;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class JdbcMeteringDao implements MeteringDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcMeteringDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String UPDATE_SQL =
        "UPDATE bbeng_metering_reading_intervals " +
        "SET `value` = `value` + ? " +
        "WHERE meter_id = ? " +
        "AND interval_start = ? " +
        "AND `timestamp` = ? " +
        "AND interval_length = ? " +
        "AND stream_suffix = ? " +
        "AND stream_number = ? " +
        "AND uom = ? " +
        "AND quality = ? " +
        "AND (method_flag <=> ?) " +
        "AND (reason_code <=> ?) " +
        "AND scaled = ?";

    private static final String INSERT_SQL =
        "INSERT INTO bbeng_metering_reading_intervals " +
        "(`meter_id`,`interval_start`,`timestamp`,`interval_length`,`stream_suffix`,`stream_number`,`uom`,`quality`,`method_flag`,`reason_code`,`scaled`,`value`) " +
        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE_TOTAL_METER_SQL =
        "UPDATE bbeng_metering_totals_meter SET total_value = total_value + ? WHERE meter_id = ?";

    private static final String INSERT_TOTAL_METER_SQL =
        "INSERT INTO bbeng_metering_totals_meter (`meter_id`,`total_value`) VALUES (?,?)";

    @Override
    public int updateIncrementValue(MeteringRecord r) {
        return jdbcTemplate.update(UPDATE_SQL,
                r.getValue(),
                r.getMeterId(),
                r.getIntervalStart(),
                r.getTimestamp(),
                r.getIntervalLength(),
                r.getStreamSuffix(),
                r.getStreamNumber(),
                r.getUom(),
                r.getQuality(),
                r.getMethodFlag(),
                r.getReasonCode(),
                r.getScaled()
        );
    }

    @Override
    public int insert(MeteringRecord r) {
        return jdbcTemplate.update(INSERT_SQL,
                r.getMeterId(),
                r.getIntervalStart(),
                r.getTimestamp(),
                r.getIntervalLength(),
                r.getStreamSuffix(),
                r.getStreamNumber(),
                r.getUom(),
                r.getQuality(),
                r.getMethodFlag(),
                r.getReasonCode(),
                r.getScaled(),
                r.getValue()
        );
    }

    @Override
    @Transactional
    public void batchUpsert(List<MeteringRecord> records) {
        for (MeteringRecord r : records) {
            int updated = updateIncrementValue(r);
            if (updated == 0) {
                insert(r);
            }
        }
    }

    @Override
    public int updateIncrementTotalByMeter(TotalByMeter t) {
        return jdbcTemplate.update(UPDATE_TOTAL_METER_SQL,
                t.getTotalValue(),
                t.getMeterId()
        );
    }

    @Override
    public int insertTotalByMeter(TotalByMeter t) {
        return jdbcTemplate.update(INSERT_TOTAL_METER_SQL,
                t.getMeterId(),
                t.getTotalValue()
        );
    }

    @Override
    @Transactional
    public void batchUpsertTotalsByMeter(List<TotalByMeter> totals) {
        for (TotalByMeter t : totals) {
            int updated = updateIncrementTotalByMeter(t);
            if (updated == 0) {
                insertTotalByMeter(t);
            }
        }
    }
}

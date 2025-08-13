package com.example.metering;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class CsvService {
    private static final Logger log = Logger.getLogger(CsvService.class.getName());

    private final MeteringDao dao;

    @Value("${csv.input.dir}")
    private String inputDir;

    @Value("${csv.archive.dir}")
    private String archiveDir;

    @Value("${csv.hasHeader:true}")
    private boolean hasHeader;

    private final java.text.SimpleDateFormat tsFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public CsvService(MeteringDao dao) {
        this.dao = dao;
        tsFormat.setLenient(false);
    }

    @Transactional
    public void importAll() {
        File dir = new File(inputDir);
        if (!dir.exists() || !dir.isDirectory()) {
            log.warning("Input directory does not exist: " + inputDir);
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null || files.length == 0) {
            log.info("No CSV files to import in: " + inputDir);
            return;
        }

        Arrays.sort(files);
        for (File f : files) {
            try {
                log.info("Importing file: " + f.getAbsolutePath());
                List<MeteringRecord> rows = parseCsv(f);


                Map<MeteringRecord, BigDecimal> agg = new LinkedHashMap<>();
                for (MeteringRecord r : rows) {
                    agg.merge(r, r.getValue(), BigDecimal::add);
                }
                List<MeteringRecord> aggregated = agg.entrySet().stream().map(e -> {
                    MeteringRecord r = e.getKey();
                    r.setValue(e.getValue());
                    return r;
                }).collect(Collectors.toList());


                Map<Integer, BigDecimal> totalsByMeter = new LinkedHashMap<>();
                for (MeteringRecord r : aggregated) {
                    totalsByMeter.merge(r.getMeterId(), r.getValue(), BigDecimal::add);
                }
                List<TotalByMeter> totalsList = new ArrayList<>();
                for (Map.Entry<Integer, BigDecimal> e : totalsByMeter.entrySet()) {
                    TotalByMeter t = new TotalByMeter();
                    t.setMeterId(e.getKey());
                    t.setTotalValue(e.getValue());
                    totalsList.add(t);
                }

                dao.batchUpsert(aggregated);
                dao.batchUpsertTotalsByMeter(totalsList);
                moveToArchive(f.toPath());
                log.info("Imported file: " + f.getName()
                         + " rows=" + rows.size()
                         + " unique=" + aggregated.size()
                         + " totalsByMeter=" + totalsList.size());
            } catch (Exception ex) {
                log.severe("Failed to import file " + f.getName() + ": " + ex.getMessage());
            }
        }
    }

    private void moveToArchive(Path file) throws IOException {
        File archive = new File(archiveDir);
        if (!archive.exists()) {
            archive.mkdirs();
        }
        String ts = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new java.util.Date());
        Path target = archive.toPath().resolve(
            file.getFileName().toString().replaceAll("\\.csv$", "") + "_" + ts + ".csv"
        );
        Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
    }

    private List<MeteringRecord> parseCsv(File file) throws IOException, ParseException {
        List<MeteringRecord> out = new ArrayList<>();
        org.apache.commons.csv.CSVFormat fmt = hasHeader ? CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim()
                                  : CSVFormat.DEFAULT.withTrim();
        try (FileReader rdr = new FileReader(file);
             CSVParser parser = new CSVParser(rdr, fmt)) {
            if (hasHeader) {
                for (CSVRecord rec : parser) {
                    out.add(fromRecordByName(rec));
                }
            } else {
                for (CSVRecord rec : parser) {
                    out.add(fromRecordByIndex(rec));
                }
            }
        }
        return out;
    }

    private MeteringRecord fromRecordByName(CSVRecord rec) throws ParseException {
        MeteringRecord r = new MeteringRecord();
        r.setMeterId(parseInt(rec.get("meter_id")));
        r.setIntervalStart(parseTs(rec.get("interval_start")));
        r.setTimestamp(parseTs(rec.get("timestamp")));
        r.setIntervalLength(parseInt(rec.get("interval_length")));
        r.setStreamSuffix(nullIfEmpty(rec.get("stream_suffix")));
        r.setStreamNumber(parseNullableInt(rec.get("stream_number")));
        r.setUom(nullIfEmpty(rec.get("uom")));
        r.setQuality(nullIfEmpty(rec.get("quality")));
        r.setMethodFlag(emptyToNull(rec.get("method_flag")));
        r.setReasonCode(parseNullableInt(rec.get("reason_code")));
        r.setScaled(nullIfEmpty(rec.get("scaled")));
        r.setValue(parseBigDecimal(rec.get("value")));
        return r;
    }

    private MeteringRecord fromRecordByIndex(CSVRecord rec) throws ParseException {
        MeteringRecord r = new MeteringRecord();
        r.setMeterId(parseInt(rec.get(0)));
        r.setIntervalStart(parseTs(rec.get(1)));
        r.setTimestamp(parseTs(rec.get(2)));
        r.setIntervalLength(parseInt(rec.get(3)));
        r.setStreamSuffix(nullIfEmpty(rec.get(4)));
        r.setStreamNumber(parseNullableInt(rec.get(5)));
        r.setUom(nullIfEmpty(rec.get(6)));
        r.setQuality(nullIfEmpty(rec.get(7)));
        r.setMethodFlag(emptyToNull(rec.get(8)));
        r.setReasonCode(parseNullableInt(rec.get(9)));
        r.setScaled(nullIfEmpty(rec.get(10)));
        r.setValue(parseBigDecimal(rec.get(11)));
        return r;
    }

    private String nullIfEmpty(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty() || s.equalsIgnoreCase("NULL")) return null;
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    private String emptyToNull(String s) {
        return nullIfEmpty(s);
    }

    private Integer parseNullableInt(String s) {
        String v = nullIfEmpty(s);
        if (v == null) return null;
        return Integer.parseInt(v);
    }

    private int parseInt(String s) {
        return Integer.parseInt(s.trim());
    }

    private java.sql.Timestamp parseTs(String s) throws ParseException {
        String v = nullIfEmpty(s);
        if (v == null) return null;
        return new java.sql.Timestamp(tsFormat.parse(v).getTime());
    }

    private java.math.BigDecimal parseBigDecimal(String s) {
        String v = s == null ? null : s.trim();
        if (v == null || v.isEmpty()) return java.math.BigDecimal.ZERO;
        if (v.equalsIgnoreCase("NULL")) return java.math.BigDecimal.ZERO;
        return new java.math.BigDecimal(v);
    }
}

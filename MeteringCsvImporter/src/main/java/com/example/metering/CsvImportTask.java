package com.example.metering;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CsvImportTask {
    private final CsvService service;

    public CsvImportTask(CsvService service) {
        this.service = service;
    }

    @Scheduled(cron = "0 * * * * *", zone = "Europe/Bucharest")
    public void run() {
        service.importAll();
    }
}

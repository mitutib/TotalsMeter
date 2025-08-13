package com.example.metering;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/import")
public class ImportController {
    private final CsvService csvService;

    public ImportController(CsvService csvService) {
        this.csvService = csvService;
    }

    @GetMapping("/run")
    public String runNow() {
        csvService.importAll();
        return "Import triggered";
    }
}

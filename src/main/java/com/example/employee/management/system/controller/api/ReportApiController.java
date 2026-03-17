package com.example.employee.management.system.controller.api;

import com.example.employee.management.system.service.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/reports")
public class ReportApiController {

    @Autowired
    private IReportService reportService;

    @GetMapping("/average-salary")
    public ResponseEntity<BigDecimal> getAverageSalary(){
        return ResponseEntity.ok(reportService.getAverageSalary());
    }

    @GetMapping("/average-age")
    public ResponseEntity<Double> getAverageAge(){
        return ResponseEntity.ok(reportService.getAverageAge());
    }

    @GetMapping("/total-employees")
    public ResponseEntity<Long> getTotalEmployees() {
        return ResponseEntity.ok(reportService.getTotalEmployees());
    }

    @GetMapping("/total-departments")
    public ResponseEntity<Long> getTotalDepartments() {
        return ResponseEntity.ok(reportService.getTotalDepartments());
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportToCSV() throws IOException {
        byte[] data = reportService.exportToCSV();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header("Content-Disposition", "attachment; filename=report.csv")
                .body(data);
    }

}

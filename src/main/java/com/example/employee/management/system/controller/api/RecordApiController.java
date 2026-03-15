package com.example.employee.management.system.controller.api;

import com.example.employee.management.system.service.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/reports")
public class RecordApiController {

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


}

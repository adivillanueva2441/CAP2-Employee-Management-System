package com.example.employee.management.system.controller;

import com.example.employee.management.system.controller.api.ReportApiController;
import com.example.employee.management.system.service.IReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportApiControllerTest {

    @Mock
    private IReportService reportService;

    @InjectMocks
    private ReportApiController reportApiController;

    @Test
    void getAverageSalary_returns200WithAverageSalary() {
        when(reportService.getAverageSalary()).thenReturn(BigDecimal.valueOf(20000.00));

        ResponseEntity<BigDecimal> response = reportApiController.getAverageSalary();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(BigDecimal.valueOf(20000.00));
        verify(reportService, times(1)).getAverageSalary();
    }

    @Test
    void getAverageAge_returns200WithAverageAge() {
        when(reportService.getAverageAge()).thenReturn(34.5);

        ResponseEntity<Double> response = reportApiController.getAverageAge();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(34.5);
        verify(reportService, times(1)).getAverageAge();
    }

    @Test
    void getTotalEmployees_returns200WithTotalCount() {
        when(reportService.getTotalEmployees()).thenReturn(100L);

        ResponseEntity<Long> response = reportApiController.getTotalEmployees();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(100L);
        verify(reportService, times(1)).getTotalEmployees();
    }

    @Test
    void getTotalDepartments_returns200WithTotalCount() {
        when(reportService.getTotalDepartments()).thenReturn(5L);

        ResponseEntity<Long> response = reportApiController.getTotalDepartments();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(5L);
        verify(reportService, times(1)).getTotalDepartments();
    }

    @Test
    void exportToCSV_returns200WithCSVData() throws IOException {
        byte[] csvData = "EMS SUMMARY REPORT\nTotal Employees,100".getBytes();
        when(reportService.exportToCSV()).thenReturn(csvData);

        ResponseEntity<byte[]> response = reportApiController.exportToCSV();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(csvData);
        assertThat(response.getHeaders().getFirst("Content-Disposition"))
                .isEqualTo("attachment; filename=report.csv");
        assertThat(Objects.requireNonNull(response.getHeaders().getContentType()).toString())
                .isEqualTo("text/csv");
        verify(reportService, times(1)).exportToCSV();
    }

    @Test
    void exportToCSV_returnsEmptyByteArray_whenNoData() throws IOException {
        when(reportService.exportToCSV()).thenReturn(new byte[0]);

        ResponseEntity<byte[]> response = reportApiController.exportToCSV();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEmpty();
        verify(reportService, times(1)).exportToCSV();
    }
}
package com.campus.scems.reports;

import com.campus.scems.service.ReportExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/faculty/reports")
public class AdminReportController {

    private final ReportExportService reportExportService;

    public AdminReportController(ReportExportService reportExportService) {
        this.reportExportService = reportExportService;
    }

    @GetMapping("/registrations.xlsx")
    public ResponseEntity<byte[]> excel() throws IOException {
        byte[] data = reportExportService.exportRegistrationsExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=registrations.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @GetMapping("/registrations.pdf")
    public ResponseEntity<byte[]> pdf() {
        byte[] data = reportExportService.exportRegistrationsPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=registrations.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}

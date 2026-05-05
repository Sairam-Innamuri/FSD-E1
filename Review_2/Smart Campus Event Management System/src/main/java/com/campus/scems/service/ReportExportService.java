package com.campus.scems.service;

import com.campus.scems.model.RegistrationEntity;
import com.campus.scems.repository.RegistrationRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportExportService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final RegistrationRepository registrationRepository;

    public ReportExportService(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public byte[] exportRegistrationsExcel() throws IOException {
        List<RegistrationEntity> rows = registrationRepository.findAllWithEvent();
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Registrations");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Registration ID");
            header.createCell(1).setCellValue("Event");
            header.createCell(2).setCellValue("Department");
            header.createCell(3).setCellValue("Student Name");
            header.createCell(4).setCellValue("Email");
            header.createCell(5).setCellValue("Roll No");
            header.createCell(6).setCellValue("Registered At");
            int r = 1;
            for (RegistrationEntity reg : rows) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(reg.getId());
                row.createCell(1).setCellValue(reg.getEvent().getTitle());
                row.createCell(2).setCellValue(reg.getEvent().getDepartment());
                row.createCell(3).setCellValue(reg.getStudentName());
                row.createCell(4).setCellValue(reg.getStudentEmail());
                row.createCell(5).setCellValue(reg.getStudentRoll());
                row.createCell(6).setCellValue(reg.getRegisteredAt().format(FMT));
            }
            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }
            wb.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportRegistrationsPdf() {
        List<RegistrationEntity> rows = registrationRepository.findAllWithEvent();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 48, 36);
        PdfWriter.getInstance(doc, out);
        doc.open();
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        doc.add(new Paragraph("Campus Event Registrations", titleFont));
        doc.add(new Paragraph(" "));
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 2.2f, 2.2f, 2f, 2.4f, 2.4f, 2f });
        addHeader(table, "Event");
        addHeader(table, "Department");
        addHeader(table, "Student");
        addHeader(table, "Email");
        addHeader(table, "Roll");
        addHeader(table, "Registered");
        for (RegistrationEntity reg : rows) {
            table.addCell(new Phrase(reg.getEvent().getTitle(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(reg.getEvent().getDepartment(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(reg.getStudentName(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(reg.getStudentEmail(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
            table.addCell(new Phrase(reg.getStudentRoll(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(reg.getRegisteredAt().format(FMT), FontFactory.getFont(FontFactory.HELVETICA, 9)));
        }
        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    private static void addHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
        cell.setPadding(6);
        table.addCell(cell);
    }
}

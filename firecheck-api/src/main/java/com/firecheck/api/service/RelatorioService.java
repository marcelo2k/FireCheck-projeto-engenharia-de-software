package com.firecheck.api.service;

import com.firecheck.api.model.Inspecao;
import com.firecheck.api.repository.InspecaoRepository;
// Imports específicos do OpenPDF (evita conflito de Font)
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

// Imports específicos do Apache POI (Excel)
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class RelatorioService {

    @Autowired
    private InspecaoRepository inspecaoRepository;

    // --- PDF ---
    public ByteArrayInputStream gerarRelatorioInspecoes() {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Usa FontFactory do OpenPDF explicitamente
            com.lowagie.text.Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Relatório Geral de Inspeções - FireCheck", fontHeader);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 3, 3, 3, 2});

            addTableHeader(table, "ID");
            addTableHeader(table, "Data");
            addTableHeader(table, "Edificação");
            addTableHeader(table, "Técnico");
            addTableHeader(table, "Status");

            List<Inspecao> inspecoes = inspecaoRepository.findAll();
            for (Inspecao i : inspecoes) {
                table.addCell(String.valueOf(i.getIdInspecao()));
                table.addCell(i.getDataInspecao() != null ? i.getDataInspecao().toString() : "");
                table.addCell(i.getEdificacao() != null ? i.getEdificacao().getNome() : "");
                table.addCell(i.getTecnico() != null ? i.getTecnico().getNomeCompleto() : "");
                table.addCell(i.getStatus());
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        header.setPhrase(new Phrase(headerTitle));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }

    // --- EXCEL ---
    public ByteArrayInputStream gerarRelatorioInspecoesExcel() throws IOException {
        String[] colunas = {"ID", "Data", "Edificação", "Técnico", "Status"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Inspeções");

            CellStyle headerCellStyle = workbook.createCellStyle();
            // Usa Font do POI explicitamente (com nome completo para garantir)
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerCellStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(headerCellStyle);
            }

            List<Inspecao> inspecoes = inspecaoRepository.findAll();
            int rowIdx = 1;
            for (Inspecao inspecao : inspecoes) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(inspecao.getIdInspecao());
                row.createCell(1).setCellValue(inspecao.getDataInspecao() != null ? inspecao.getDataInspecao().toString() : "");
                row.createCell(2).setCellValue(inspecao.getEdificacao() != null ? inspecao.getEdificacao().getNome() : "");
                row.createCell(3).setCellValue(inspecao.getTecnico() != null ? inspecao.getTecnico().getNomeCompleto() : "");
                row.createCell(4).setCellValue(inspecao.getStatus());
            }
            
            for(int i = 0; i < colunas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
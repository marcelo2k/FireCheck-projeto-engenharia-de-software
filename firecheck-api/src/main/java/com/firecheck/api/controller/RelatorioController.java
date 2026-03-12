package com.firecheck.api.controller;

import com.firecheck.api.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/inspecoes/pdf")
    public ResponseEntity<InputStreamResource> relatorioInspecoesPdf() {
        ByteArrayInputStream bis = relatorioService.gerarRelatorioInspecoes();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=relatorio_inspecoes.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    // --- NOVO ENDPOINT EXCEL ---
    @GetMapping("/inspecoes/excel")
    public ResponseEntity<InputStreamResource> relatorioInspecoesExcel() throws IOException {
        ByteArrayInputStream bis = relatorioService.gerarRelatorioInspecoesExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=relatorio_inspecoes.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(bis));
    }
}
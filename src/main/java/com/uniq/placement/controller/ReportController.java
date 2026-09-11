package com.uniq.placement.controller;

import com.uniq.placement.dto.report.CompanyReportRowDto;
import com.uniq.placement.dto.report.OutstandingRowDto;
import com.uniq.placement.dto.report.PaymentWithCandidateDto;
import com.uniq.placement.dto.report.ReportOverviewDto;
import com.uniq.placement.entity.enums.PaymentType;
import com.uniq.placement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/overview")
    public ResponseEntity<ReportOverviewDto> getOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String team) {
        return ResponseEntity.ok(reportService.getOverview(from, to, team));
    }

    @GetMapping("/collections")
    public ResponseEntity<List<PaymentWithCandidateDto>> getCollections(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String team,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String account,
            @RequestParam(required = false) PaymentType type,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(reportService.getCollections(from, to, team, userId, account, type, search));
    }

    @GetMapping("/outstanding")
    public ResponseEntity<List<OutstandingRowDto>> getOutstanding(
            @RequestParam(required = false) String team,
            @RequestParam(required = false) String bucket,
            @RequestParam(defaultValue = "false") boolean overdueOnly) {
        return ResponseEntity.ok(reportService.getOutstanding(team, bucket, overdueOnly));
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyReportRowDto>> getCompanies(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String team) {
        return ResponseEntity.ok(reportService.getCompanies(from, to, team));
    }

    @GetMapping(value = "/collections.csv", produces = "text/csv")
    public ResponseEntity<String> getCollectionsCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String team) {
        return ResponseEntity.ok(reportService.getCollectionsCsv(from, to, team));
    }
}

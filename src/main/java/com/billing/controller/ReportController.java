package com.billing.controller;

import com.billing.dto.SalesReport;
import com.billing.service.ReportService;
import com.billing.service.UserService;
import com.billing.util.ReportPdf;
import java.security.Principal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** General total-sales report (optional date range) and its PDF download. */
@Controller
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    public ReportController(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @GetMapping("/report")
    public String report(
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {
        if (invalidRange(from, to)) {
            model.addAttribute("error", "The start date must not be after the end date.");
            from = null;
            to = null;
        }
        SalesReport report = reportService.build(from, to);
        model.addAttribute("report", report);
        model.addAttribute("fromText", from == null ? "" : from.toString());   // yyyy-MM-dd, as <input type=date> expects
        model.addAttribute("toText", to == null ? "" : to.toString());
        model.addAttribute("period", ReportService.describePeriod(from, to));
        model.addAttribute("generatedAt", java.time.LocalDateTime.now());
        return "report";
    }

    @GetMapping("/report/pdf")
    public ResponseEntity<byte[]> reportPdf(
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal) {
        if (invalidRange(from, to)) {
            from = null;
            to = null;
        }
        SalesReport report = reportService.build(from, to);
        String userName = userService.findByEmail(principal.getName())
                .map(u -> u.getFullName())
                .orElse(principal.getName());
        byte[] pdf = ReportPdf.render(report, ReportService.describePeriod(from, to), userName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sales_report_" + LocalDate.now() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private static boolean invalidRange(LocalDate from, LocalDate to) {
        return from != null && to != null && from.isAfter(to);
    }
}

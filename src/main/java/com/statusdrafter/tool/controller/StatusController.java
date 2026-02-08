package com.statusdrafter.tool.controller;

import com.statusdrafter.tool.dto.StatusRequest;
import com.statusdrafter.tool.dto.StatusResponse;
import com.statusdrafter.tool.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import com.statusdrafter.tool.repository.UserRepository;
import com.statusdrafter.tool.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/status")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<StatusResponse> submitStatus(@RequestBody StatusRequest request, Principal principal) {
        return new ResponseEntity<>(statusService.processStatus(request, principal.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/daily")
    public ResponseEntity<List<StatusResponse>> getDailyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(statusService.getDailyReport(targetDate));
    }

    @GetMapping("/history")
    public ResponseEntity<List<StatusResponse>> getMyHistory(Principal principal) {
        return ResponseEntity.ok(statusService.getUserHistory(principal.getName()));
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<String> exportCsv(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=status_report.csv")
                .body(statusService.exportToCsv(targetDate));
    }

    @GetMapping(value = "/export/txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> exportTxt(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=status_report.txt")
                .body(statusService.exportToTxt(targetDate));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Status Drafter Tool is Online!");
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> whoami(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        return ResponseEntity.ok(Map.of(
            "name", user.getFullName(),
            "role", user.getRole(),
            "username", user.getUsername()
        ));
    }
}

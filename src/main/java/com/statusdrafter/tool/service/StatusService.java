package com.statusdrafter.tool.service;

import com.statusdrafter.tool.dto.StatusRequest;
import com.statusdrafter.tool.dto.StatusResponse;
import com.statusdrafter.tool.engine.StatusParsingEngine;
import com.statusdrafter.tool.entity.StatusRecord;
import com.statusdrafter.tool.entity.User;
import com.statusdrafter.tool.repository.StatusRecordRepository;
import com.statusdrafter.tool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRecordRepository statusRepository;
    private final UserRepository userRepository;
    private final StatusParsingEngine parsingEngine;

    @Transactional
    public StatusResponse processStatus(StatusRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StatusParsingEngine.ParsedData parsedData = parsingEngine.parse(request.getRawContent());

        StatusRecord record = new StatusRecord();
        record.setUser(user);
        record.setRawContent(request.getRawContent());
        record.setParsedYesterday(String.join("; ", parsedData.yesterday()));
        record.setParsedToday(String.join("; ", parsedData.today()));
        record.setParsedBlockers(String.join("; ", parsedData.blockers()));
        record.setTotalHoursSpent(parsedData.hoursSpent());
        record.setConfidenceScore(parsedData.confidenceScore());
        record.setStatusDate(LocalDate.now());

        StatusRecord saved = statusRepository.save(record);

        return mapToResponse(saved, parsedData);
    }

    public List<StatusResponse> getDailyReport(LocalDate date) {
        return statusRepository.findAllByStatusDate(date).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<StatusResponse> getUserHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return statusRepository.findAllByUserIdAndStatusDateBetween(user.getId(), 
                LocalDate.now().minusDays(30), LocalDate.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public String exportToCsv(LocalDate date) {
        List<StatusRecord> records = statusRepository.findAllByStatusDate(date);
        StringBuilder csv = new StringBuilder("User,Yesterday,Today,Blockers,Hours\n");
        for (StatusRecord r : records) {
            csv.append(String.format("%s,\"%s\",\"%s\",\"%s\",%s\n",
                    r.getUser().getUsername(),
                    r.getParsedYesterday(),
                    r.getParsedToday(),
                    r.getParsedBlockers(),
                    r.getTotalHoursSpent()));
        }
        return csv.toString();
    }

    public String exportToTxt(LocalDate date) {
        List<StatusRecord> records = statusRepository.findAllByStatusDate(date);
        StringBuilder txt = new StringBuilder("TEAM STATUS REPORT - " + date + "\n\n");
        for (StatusRecord r : records) {
            txt.append("USER: ").append(r.getUser().getFullName()).append("\n");
            txt.append("YESTERDAY: ").append(r.getParsedYesterday()).append("\n");
            txt.append("TODAY: ").append(r.getParsedToday()).append("\n");
            txt.append("BLOCKERS: ").append(r.getParsedBlockers()).append("\n");
            txt.append("HOURS: ").append(r.getTotalHoursSpent()).append("\n");
            txt.append("------------------------------------------\n");
        }
        return txt.toString();
    }

    private StatusResponse mapToResponse(StatusRecord record, StatusParsingEngine.ParsedData parsedData) {
        return StatusResponse.builder()
                .id(record.getId())
                .userName(record.getUser().getFullName())
                .statusDate(record.getStatusDate())
                .confidenceScore(record.getConfidenceScore())
                .formattedReport(StatusResponse.ParsedDetails.builder()
                        .yesterday(parsedData.yesterday())
                        .today(parsedData.today())
                        .blockers(parsedData.blockers())
                        .hoursSpent(record.getTotalHoursSpent())
                        .build())
                .build();
    }

    private StatusResponse mapToResponse(StatusRecord record) {
        // Overloaded map method for retrieval
        return StatusResponse.builder()
                .id(record.getId())
                .userName(record.getUser().getFullName())
                .statusDate(record.getStatusDate())
                .confidenceScore(record.getConfidenceScore())
                .formattedReport(StatusResponse.ParsedDetails.builder()
                        .yesterday(List.of(record.getParsedYesterday().split("; ")))
                        .today(List.of(record.getParsedToday().split("; ")))
                        .blockers(List.of(record.getParsedBlockers().split("; ")))
                        .hoursSpent(record.getTotalHoursSpent())
                        .build())
                .build();
    }
}

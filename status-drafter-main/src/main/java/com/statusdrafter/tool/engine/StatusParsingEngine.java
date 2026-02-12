package com.statusdrafter.tool.engine;

import com.statusdrafter.tool.entity.ParsingRule;
import com.statusdrafter.tool.repository.ParsingRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StatusParsingEngine {

    private final ParsingRuleRepository ruleRepository;

    // Regex to extract hours like "3h", "3 hrs", "2.5 hours"
    private static final Pattern HOUR_PATTERN = Pattern.compile("(\\d+(\\.\\d+)?)\\s*(h|hr|hrs|hour|hours)", Pattern.CASE_INSENSITIVE);

    public ParsedData parse(String rawText) {
        List<ParsingRule> activeRules = ruleRepository.findAllByIsActiveTrue();
        
        // Tokenize by common sentence delimiters
        String[] sentences = rawText.split("[.\\n;]+");
        
        List<String> yesterdayItems = new ArrayList<>();
        List<String> todayItems = new ArrayList<>();
        List<String> blockerItems = new ArrayList<>();
        double totalHours = 0.0;

        for (String sentence : sentences) {
            String cleanSentence = sentence.trim();
            if (cleanSentence.isEmpty()) continue;

            // Extract hours if present
            Matcher hourMatcher = HOUR_PATTERN.matcher(cleanSentence);
            if (hourMatcher.find()) {
                totalHours += Double.parseDouble(hourMatcher.group(1));
            }

            // Classify based on rules
            Optional<ParsingRule> matchedRule = activeRules.stream()
                    .filter(rule -> cleanSentence.toLowerCase().contains(rule.getKeyword().toLowerCase()))
                    .max(Comparator.comparingInt(ParsingRule::getPriority));

            if (matchedRule.isPresent()) {
                String result = normalize(cleanSentence, matchedRule.get().getKeyword());
                switch (matchedRule.get().getCategory()) {
                    case YESTERDAY -> yesterdayItems.add(result);
                    case TODAY -> todayItems.add(result);
                    case BLOCKERS -> blockerItems.add(result);
                }
            } else {
                // Heuristic backup: if it contains "stuck" or "issue", it's likely a blocker
                if (cleanSentence.toLowerCase().contains("stuck") || cleanSentence.toLowerCase().contains("issue")) {
                    blockerItems.add(cleanSentence);
                } else if (cleanSentence.toLowerCase().contains("will") || cleanSentence.toLowerCase().contains("plan")) {
                    todayItems.add(cleanSentence);
                } else {
                    // Default to yesterday if no clues found
                    yesterdayItems.add(cleanSentence);
                }
            }
        }

        double confidence = calculateConfidence(sentences.length, yesterdayItems.size() + todayItems.size() + blockerItems.size());

        return new ParsedData(yesterdayItems, todayItems, blockerItems, totalHours, confidence);
    }

    private String normalize(String sentence, String keyword) {
        // Remove the keyword and common filler words at the start
        String result = sentence.replaceAll("(?i)" + Pattern.quote(keyword), "").trim();
        result = result.replaceAll("(?i)^(I |we |am |will |be |to |the |a |an )+", "").trim();
        // Capitalize first letter
        if (!result.isEmpty()) {
            result = result.substring(0, 1).toUpperCase() + result.substring(1);
        }
        return result;
    }

    private double calculateConfidence(int totalSentences, int classifiedSentences) {
        if (totalSentences == 0) return 0.0;
        return (double) classifiedSentences / totalSentences;
    }

    public record ParsedData(
            List<String> yesterday,
            List<String> today,
            List<String> blockers,
            double hoursSpent,
            double confidenceScore
    ) {}
}

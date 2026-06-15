package com.resumeanalyzer.resumeanalyzer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String analyzeResume(String resumeText) {
        String prompt = "Analyze this resume and respond ONLY in this exact format, with no extra text, no markdown, no asterisks, before or after:\n\n" +
            "ATS_SCORE: <a single number out of 100>\n" +
            "ATS_REASON: <one paragraph explanation of the score>\n" +
            "SKILLS_FOUND: <comma-separated list of skills found in the resume>\n" +
            "MISSING_SKILLS: <comma-separated list of skills that are missing but relevant>\n" +
            "JOB_RECOMMENDATIONS: <exactly 5 entries, each formatted as 'Job Title: one sentence reason', separated by a pipe character |>\n" +
            "IMPROVEMENT_SUGGESTIONS: <list of suggestions, each separated by a pipe character |>\n\n" +
            "Resume:\n" + resumeText;

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.1-8b-instant");
        body.put("messages", List.of(message));
        body.put("max_tokens", 1000);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_URL, request, Map.class);
            Map responseBody = response.getBody();
            List choices = (List) responseBody.get("choices");
            Map choice = (Map) choices.get(0);
            Map messageResponse = (Map) choice.get("message");
            return (String) messageResponse.get("content");
        } catch (Exception e) {
            return "Error analyzing resume: " + e.getMessage();
        }
    }
}
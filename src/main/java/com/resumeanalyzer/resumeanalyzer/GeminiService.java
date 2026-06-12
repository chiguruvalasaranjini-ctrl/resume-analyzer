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
        String prompt = "Analyze this resume and provide: 1. ATS Score (out of 100) 2. Skills Found 3. Missing Skills 4. Job Recommendations (5 roles) 5. Improvement Suggestions. Resume: " + resumeText;

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
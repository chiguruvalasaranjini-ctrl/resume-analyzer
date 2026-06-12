package com.resumeanalyzer.resumeanalyzer;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {

        Map<String, String> response = new HashMap<>();

        try {
            PDDocument document = Loader.loadPDF(file.getBytes());
            PDFTextStripper stripper = new PDFTextStripper();
            String extractedText = stripper.getText(document);
            document.close();

            // Analyze with Gemini AI
            String analysisResult = geminiService.analyzeResume(extractedText);

            Resume resume = new Resume();
            resume.setUserId(userId);
            resume.setFileName(file.getOriginalFilename());
            resume.setExtractedText(extractedText);
            resume.setAnalysisResult(analysisResult);
            resume.setUploadedAt(LocalDateTime.now().toString());

            resumeRepository.save(resume);

            response.put("message", "Resume analyzed successfully!");
            response.put("analysis", analysisResult);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
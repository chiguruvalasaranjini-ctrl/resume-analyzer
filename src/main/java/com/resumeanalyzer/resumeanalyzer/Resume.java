package com.resumeanalyzer.resumeanalyzer;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    @Column(columnDefinition = "TEXT")
    private String analysisResult;

    private String uploadedAt;
}
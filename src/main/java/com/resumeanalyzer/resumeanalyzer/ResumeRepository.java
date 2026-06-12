package com.resumeanalyzer.resumeanalyzer;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);
}
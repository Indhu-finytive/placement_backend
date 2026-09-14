package com.uniq.placement.util;

import com.uniq.placement.repository.CandidateRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CandidateCodeGenerator {

    private final CandidateRepository candidateRepository;

    public CandidateCodeGenerator(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public synchronized String generateCode(String course) {
        String prefix = getCoursePrefix(course);
        int maxSequence = candidateRepository.findMaxSequenceByPrefix(prefix);
        return String.format("%s-%05d", prefix, maxSequence + 1);
    }

    private String getCoursePrefix(String course) {
        if (course == null) return "UNIQ-GEN";
        String upperCourse = course.trim().toUpperCase();
        if (upperCourse.contains("PRODUCTION SUPPORT")) return "UNIQ-PS";
        if (upperCourse.contains("JAVA") || upperCourse.contains("FULL STACK")) return "UNIQ-JFS";
        if (upperCourse.contains("DATA ANALYTICS")) return "UNIQ-DA";
        if (upperCourse.contains("TESTING")) return "UNIQ-ST";
        
        // generic fallback based on initials
        String[] parts = upperCourse.split("\\s+");
        StringBuilder sb = new StringBuilder("UNIQ-");
        for (String part : parts) {
            if (!part.isEmpty()) sb.append(part.charAt(0));
        }
        return sb.toString();
    }
}

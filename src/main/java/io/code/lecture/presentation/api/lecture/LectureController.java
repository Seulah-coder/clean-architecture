package io.code.lecture.presentation.api.lecture;

import io.code.lecture.application.lecture.*;
import io.code.lecture.application.lecture.dto.LectureListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
public class LectureController {
    
    private final LectureService lectureService;
    
    /**
     * 2️⃣ 특강 선택 API (날짜별 신청 가능한 특강 목록)
     */
    @GetMapping
    public ResponseEntity<LectureListResponse> getAvailableLectures(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        LectureListResponse response = lectureService.getAvailableLectures(date);
        return ResponseEntity.ok(response);
    }
}
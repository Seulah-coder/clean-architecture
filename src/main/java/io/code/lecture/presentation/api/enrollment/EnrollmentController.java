package io.code.lecture.presentation.api.enrollment;

import io.code.lecture.application.enrollment.*;
import io.code.lecture.application.enrollment.dto.*;
import io.code.lecture.presentation.api.enrollment.request.EnrollmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    
    private final EnrollmentService enrollmentService;
    
    /**
     * 1️⃣ 특강 신청 API
     */
    @PostMapping
    public ResponseEntity<EnrollmentInfo> enroll(@RequestBody EnrollmentRequest request) {
        EnrollmentCommand command = new EnrollmentCommand(
            request.getUserId(), 
            request.getLectureId()
        );
        
        EnrollmentInfo result = enrollmentService.enroll(command);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 3️⃣ 특강 신청 완료 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<EnrollmentListResponse> getEnrollments(@RequestParam Long userId) {
        EnrollmentListResponse response = enrollmentService.getEnrollments(userId);
        return ResponseEntity.ok(response);
    }
}
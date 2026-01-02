package io.code.lecture.application.enrollment;

import io.code.lecture.domain.enrollment.*;
import io.code.lecture.domain.lecture.*;
import io.code.lecture.application.enrollment.dto.*;
import io.code.lecture.common.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    
    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    
    @Transactional
    public EnrollmentInfo enroll(EnrollmentCommand command) {
        Long userId = command.getUserId();
        Long lectureId = command.getLectureId();
        if (userId == null) {
            throw new NullPointerException("userId는 null일 수 없습니다.");
        }
        
        if (enrollmentRepository.existsByUserIdAndLectureId(userId, lectureId)) {
            throw new DuplicateEnrollmentException("이미 신청한 특강입니다.");
        }
        
        Lecture lecture = lectureRepository.findByIdWithLock(lectureId)
            .orElseThrow(() -> new LectureNotFoundException("특강을 찾을 수 없습니다."));
        
        lecture.incrementEnrollment();
        
        Enrollment enrollment = Enrollment.builder()
            .userId(userId)
            .lectureId(lectureId)
            .build();  // enrolledAt은 @Builder.Default로 자동 설정
        
        Enrollment saved = enrollmentRepository.save(enrollment);
        lectureRepository.save(lecture);
        
        return EnrollmentInfo.from(saved, lecture);
    }
    
    @Transactional(readOnly = true)
    public EnrollmentListResponse getEnrollments(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        
        List<EnrollmentInfo> enrollmentInfos = enrollments.stream()
            .map(enrollment -> {
                Lecture lecture = lectureRepository.findById(enrollment.getLectureId())
                    .orElseThrow(() -> new LectureNotFoundException("특강을 찾을 수 없습니다."));
                return EnrollmentInfo.from(enrollment, lecture);
            })
            .collect(Collectors.toList());
        
        return new EnrollmentListResponse(enrollmentInfos);
    }
}

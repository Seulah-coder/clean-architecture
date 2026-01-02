package io.code.lecture.application.enrollment.dto;

import io.code.lecture.domain.enrollment.Enrollment;
import io.code.lecture.domain.lecture.Lecture;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EnrollmentInfo {
    private final Long enrollmentId;
    private final Long lectureId;
    private final String lectureTitle;
    private final String instructor;
    private final LocalDateTime enrolledAt;
    
    private EnrollmentInfo(Long enrollmentId, Long lectureId, String lectureTitle, 
                           String instructor, LocalDateTime enrolledAt) {
        this.enrollmentId = enrollmentId;
        this.lectureId = lectureId;
        this.lectureTitle = lectureTitle;
        this.instructor = instructor;
        this.enrolledAt = enrolledAt;
    }
    
    public static EnrollmentInfo from(Enrollment enrollment, Lecture lecture) {
        return new EnrollmentInfo(
            enrollment.getId(),
            lecture.getId(),
            lecture.getTitle(),
            lecture.getInstructor(),
            enrollment.getEnrolledAt()
        );
    }
}
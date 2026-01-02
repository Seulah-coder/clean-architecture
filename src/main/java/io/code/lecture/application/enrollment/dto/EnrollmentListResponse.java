package io.code.lecture.application.enrollment.dto;
import lombok.Getter;
import java.util.List;

@Getter
public class EnrollmentListResponse {
    private final List<EnrollmentInfo> enrollments;
    public EnrollmentListResponse(List<EnrollmentInfo> enrollments) {
        this.enrollments = enrollments;
    }
}

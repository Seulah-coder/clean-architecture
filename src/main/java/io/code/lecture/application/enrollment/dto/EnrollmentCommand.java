package io.code.lecture.application.enrollment.dto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EnrollmentCommand {
    private final Long userId;
    private final Long lectureId;
}

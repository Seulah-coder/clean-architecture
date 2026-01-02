package io.code.lecture.presentation.api.enrollment.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EnrollmentRequest {
    private Long userId;
    private Long lectureId;
}

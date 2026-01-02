package io.code.lecture.domain.enrollment;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class Enrollment {
    private Long id;
    private Long userId;
    private Long lectureId;
    
    @Builder.Default
    private LocalDateTime enrolledAt = LocalDateTime.now();
}

package io.code.lecture.domain.enrollment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Enrollment 도메인 테스트")
class EnrollmentTest {

    @Test
    @DisplayName("수강 신청 생성 시 신청 시간이 자동으로 설정된다")
    void createEnrollment() {
        // given
        Long userId = 1L;
        Long lectureId = 1L;
        LocalDateTime before = LocalDateTime.now();

        // when
        Enrollment enrollment = Enrollment.builder()
                .userId(userId)
                .lectureId(lectureId)
                .build();  // enrolledAt은 @Builder.Default로 자동 설정

        LocalDateTime after = LocalDateTime.now();

        // then
        assertThat(enrollment.getUserId()).isEqualTo(userId);
        assertThat(enrollment.getLectureId()).isEqualTo(lectureId);
        assertThat(enrollment.getEnrolledAt()).isNotNull();
        assertThat(enrollment.getEnrolledAt())
                .isAfterOrEqualTo(before)
                .isBeforeOrEqualTo(after);
    }
}

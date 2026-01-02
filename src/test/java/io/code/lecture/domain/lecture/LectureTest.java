package io.code.lecture.domain.lecture;

import io.code.lecture.common.exception.EnrollmentFullException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Lecture 도메인 테스트")
class LectureTest {

    @Test
    @DisplayName("특강 생성 시 정원은 30명으로 초기화된다")
    void createLecture() {
        // given & when
        Lecture lecture = Lecture.builder()
                .id(1L)
                .title("클린 아키텍처")
                .instructor("김영한")
                .lectureDate(LocalDate.of(2024, 6, 1))
                .build();

        // then
        assertThat(lecture.getMaxCapacity()).isEqualTo(30);
        assertThat(lecture.getCurrentEnrollment()).isEqualTo(0);
        assertThat(lecture.canEnroll()).isTrue();
    }

    @Test
    @DisplayName("수강 신청 시 현재 신청자 수가 증가한다")
    void incrementEnrollment_success() {
        // given
        Lecture lecture = createTestLecture();

        // when
        lecture.incrementEnrollment();

        // then
        assertThat(lecture.getCurrentEnrollment()).isEqualTo(1);
    }

    @Test
    @DisplayName("정원이 30명일 때 31번째 신청은 실패한다")
    void incrementEnrollment_fail_when_full() {
        // given
        Lecture lecture = createTestLecture();
        for (int i = 0; i < 30; i++) {
            lecture.incrementEnrollment();
        }

        // when & then
        assertThatThrownBy(() -> lecture.incrementEnrollment())
                .isInstanceOf(EnrollmentFullException.class)
                .hasMessageContaining("정원이 초과되었습니다");
    }

    @Test
    @DisplayName("현재 신청자가 29명일 때는 신청 가능하다")
    void canEnroll_when_29_enrolled() {
        // given
        Lecture lecture = createTestLecture();
        for (int i = 0; i < 29; i++) {
            lecture.incrementEnrollment();
        }

        // when & then
        assertThat(lecture.canEnroll()).isTrue();
        assertThat(lecture.getAvailableSeats()).isEqualTo(1);
    }

    @Test
    @DisplayName("현재 신청자가 30명일 때는 신청 불가능하다")
    void cannotEnroll_when_30_enrolled() {
        // given
        Lecture lecture = createTestLecture();
        for (int i = 0; i < 30; i++) {
            lecture.incrementEnrollment();
        }

        // when & then
        assertThat(lecture.canEnroll()).isFalse();
        assertThat(lecture.getAvailableSeats()).isEqualTo(0);
    }

    private Lecture createTestLecture() {
        return Lecture.builder()
                .id(1L)
                .title("TDD 실전")
                .instructor("박성철")
                .lectureDate(LocalDate.now().plusDays(7))
                .build();
    }
}

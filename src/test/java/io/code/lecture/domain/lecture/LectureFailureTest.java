package io.code.lecture.domain.lecture;

import io.code.lecture.common.exception.EnrollmentFullException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Lecture 도메인 실패 케이스 테스트")
class LectureFailureTest {

    @Test
    @DisplayName("정원이 가득 찬 상태에서 신청하면 예외 발생")
    void enrollmentFull_throwsException() {
        // given
        Lecture lecture = Lecture.builder()
                .id(1L)
                .title("TDD 특강")
                .instructor("박성철")
                .lectureDate(LocalDate.now().plusDays(7))
                .currentEnrollment(30)  // 이미 30명
                .build();

        // when & then
        assertThatThrownBy(() -> lecture.incrementEnrollment())
                .isInstanceOf(EnrollmentFullException.class)
                .hasMessageContaining("정원이 초과되었습니다");
    }

    @Test
    @DisplayName("정원 30명 도달 후 canEnroll은 false 반환")
    void canEnroll_returnsFalse_whenFull() {
        // given
        Lecture lecture = Lecture.builder()
                .id(1L)
                .title("DDD 특강")
                .instructor("최범균")
                .lectureDate(LocalDate.now().plusDays(7))
                .currentEnrollment(30)
                .build();

        // when
        boolean canEnroll = lecture.canEnroll();

        // then
        assertThat(canEnroll).isFalse();
        assertThat(lecture.getAvailableSeats()).isEqualTo(0);
    }

    @Test
    @DisplayName("정원 초과 상태에서 incrementEnrollment 연속 호출 시 모두 실패")
    void incrementEnrollment_multipleAttempts_allFail() {
        // given
        Lecture lecture = Lecture.builder()
                .id(1L)
                .title("Spring Boot")
                .instructor("토비")
                .lectureDate(LocalDate.now().plusDays(7))
                .currentEnrollment(30)
                .build();

        // when & then
        assertThatThrownBy(() -> lecture.incrementEnrollment())
                .isInstanceOf(EnrollmentFullException.class);
        
        assertThatThrownBy(() -> lecture.incrementEnrollment())
                .isInstanceOf(EnrollmentFullException.class);
        
        assertThat(lecture.getCurrentEnrollment()).isEqualTo(30);
    }

    @Test
    @DisplayName("29명 상태에서 1명 신청 후 추가 신청 시도하면 실패")
    void enrollmentSequence_lastSlotTaken() {
        // given
        Lecture lecture = Lecture.builder()
                .id(1L)
                .title("동시성 프로그래밍")
                .instructor("이일민")
                .lectureDate(LocalDate.now().plusDays(7))
                .currentEnrollment(29)
                .build();

        // when
        lecture.incrementEnrollment();  // 30명째 (성공)

        // then
        assertThat(lecture.getCurrentEnrollment()).isEqualTo(30);
        assertThatThrownBy(() -> lecture.incrementEnrollment())
                .isInstanceOf(EnrollmentFullException.class);
    }
}

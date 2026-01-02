package io.code.lecture.integration;

import io.code.lecture.application.enrollment.EnrollmentService;
import io.code.lecture.application.enrollment.dto.EnrollmentCommand;
import io.code.lecture.common.exception.DuplicateEnrollmentException;
import io.code.lecture.common.exception.EnrollmentFullException;
import io.code.lecture.common.exception.LectureNotFoundException;
import io.code.lecture.domain.enrollment.EnrollmentRepository;
import io.code.lecture.domain.lecture.Lecture;
import io.code.lecture.domain.lecture.LectureRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("특강 신청 실패 시나리오 통합 테스트")
class EnrollmentFailureIntegrationTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    @DisplayName("존재하지 않는 특강 ID로 신청 시 예외 발생")
    void enroll_lectureNotFound() {
        EnrollmentCommand command = new EnrollmentCommand(1L, 999L);

        assertThatThrownBy(() -> enrollmentService.enroll(command))
                .isInstanceOf(LectureNotFoundException.class)
                .hasMessageContaining("특강을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("정원이 가득 찬 특강에 신청 시 예외 발생")
    void enroll_fullCapacity() {
        Lecture lecture = Lecture.builder()
                .title("마감 특강")
                .instructor("강사")
                .lectureDate(LocalDate.now().plusDays(7))
                .currentEnrollment(30)
                .build();
        lecture = lectureRepository.save(lecture);

        final Long lectureId = lecture.getId();
        EnrollmentCommand command = new EnrollmentCommand(1L, lectureId);

        assertThatThrownBy(() -> enrollmentService.enroll(command))
                .isInstanceOf(EnrollmentFullException.class)
                .hasMessageContaining("정원이 초과되었습니다");
    }

    @Test
    @DisplayName("동일 사용자가 같은 특강 중복 신청 시 예외")
    void enroll_duplicate() {
        Lecture lecture = Lecture.builder()
                .title("테스트 특강")
                .instructor("강사")
                .lectureDate(LocalDate.now().plusDays(7))
                .build();
        lecture = lectureRepository.save(lecture);

        final Long lectureId = lecture.getId();
        EnrollmentCommand command = new EnrollmentCommand(1L, lectureId);
        enrollmentService.enroll(command);

        assertThatThrownBy(() -> enrollmentService.enroll(command))
                .isInstanceOf(DuplicateEnrollmentException.class)
                .hasMessageContaining("이미 신청한 특강입니다");

        int count = enrollmentRepository.countByLectureId(lectureId);
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("30명 신청 완료 후 31번째 신청자는 실패")
    void enroll_31st_fails() {
        Lecture lecture = Lecture.builder()
                .title("인기 특강")
                .instructor("강사")
                .lectureDate(LocalDate.now().plusDays(7))
                .build();
        lecture = lectureRepository.save(lecture);

        final Long lectureId = lecture.getId();

        for (int i = 1; i <= 30; i++) {
            enrollmentService.enroll(new EnrollmentCommand((long) i, lectureId));
        }

        EnrollmentCommand command31 = new EnrollmentCommand(31L, lectureId);
        assertThatThrownBy(() -> enrollmentService.enroll(command31))
                .isInstanceOf(EnrollmentFullException.class);

        int count = enrollmentRepository.countByLectureId(lectureId);
        assertThat(count).isEqualTo(30);
    }

    @Test
    @DisplayName("같은 사용자가 여러 특강 신청 후 재신청 시 실패")
    void enroll_multipleEnrollmentsThenDuplicate() {
        Lecture lecture1 = lectureRepository.save(Lecture.builder()
                .title("특강1")
                .instructor("강사1")
                .lectureDate(LocalDate.now().plusDays(7))
                .build());

        Lecture lecture2 = lectureRepository.save(Lecture.builder()
                .title("특강2")
                .instructor("강사2")
                .lectureDate(LocalDate.now().plusDays(14))
                .build());

        Long userId = 1L;
        final Long lecture1Id = lecture1.getId();
        final Long lecture2Id = lecture2.getId();

        enrollmentService.enroll(new EnrollmentCommand(userId, lecture1Id));
        enrollmentService.enroll(new EnrollmentCommand(userId, lecture2Id));

        assertThatThrownBy(() -> 
            enrollmentService.enroll(new EnrollmentCommand(userId, lecture1Id))
        ).isInstanceOf(DuplicateEnrollmentException.class);

        assertThatThrownBy(() -> 
            enrollmentService.enroll(new EnrollmentCommand(userId, lecture2Id))
        ).isInstanceOf(DuplicateEnrollmentException.class);
    }
}

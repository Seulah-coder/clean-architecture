package io.code.lecture.integration;

import io.code.lecture.application.enrollment.EnrollmentService;
import io.code.lecture.application.enrollment.dto.EnrollmentCommand;
import io.code.lecture.common.exception.DuplicateEnrollmentException;
import io.code.lecture.domain.enrollment.EnrollmentRepository;
import io.code.lecture.domain.lecture.Lecture;
import io.code.lecture.domain.lecture.LectureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("특강 중복 신청 통합 테스트")
class EnrollmentDuplicateTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private Lecture lecture;

    @BeforeEach
    void setUp() {
        lecture = Lecture.builder()
                .title("클린 아키텍처 특강")
                .instructor("김영한")
                .lectureDate(LocalDate.now().plusDays(7))
                .build();
        lecture = lectureRepository.save(lecture);
    }

    @Test
    @DisplayName("동일한 사용자가 같은 특강을 5번 신청하면 1번만 성공한다")
    void enroll_same_user_5_times_only_1_success() throws InterruptedException {
        Long userId = 1L;
        int attemptCount = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(attemptCount);
        CountDownLatch latch = new CountDownLatch(attemptCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < attemptCount; i++) {
            executorService.submit(() -> {
                try {
                    EnrollmentCommand command = new EnrollmentCommand(userId, lecture.getId());
                    enrollmentService.enroll(command);
                    successCount.incrementAndGet();
                } catch (DuplicateEnrollmentException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(4);

        boolean exists = enrollmentRepository.existsByUserIdAndLectureId(userId, lecture.getId());
        assertThat(exists).isTrue();

        Lecture updatedLecture = lectureRepository.findById(lecture.getId()).orElseThrow();
        assertThat(updatedLecture.getCurrentEnrollment()).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 신청한 사용자가 다시 신청하면 예외가 발생한다")
    void enroll_duplicate_throws_exception() {
        Long userId = 1L;
        EnrollmentCommand command = new EnrollmentCommand(userId, lecture.getId());
        enrollmentService.enroll(command);

        assertThatThrownBy(() -> enrollmentService.enroll(command))
                .isInstanceOf(DuplicateEnrollmentException.class)
                .hasMessageContaining("이미 신청한 특강입니다");
    }

    @Test
    @DisplayName("서로 다른 사용자는 같은 특강에 모두 신청 가능하다")
    void enroll_different_users_success() {
        Long user1 = 1L;
        Long user2 = 2L;
        Long user3 = 3L;

        enrollmentService.enroll(new EnrollmentCommand(user1, lecture.getId()));
        enrollmentService.enroll(new EnrollmentCommand(user2, lecture.getId()));
        enrollmentService.enroll(new EnrollmentCommand(user3, lecture.getId()));

        Lecture updatedLecture = lectureRepository.findById(lecture.getId()).orElseThrow();
        assertThat(updatedLecture.getCurrentEnrollment()).isEqualTo(3);

        assertThat(enrollmentRepository.existsByUserIdAndLectureId(user1, lecture.getId())).isTrue();
        assertThat(enrollmentRepository.existsByUserIdAndLectureId(user2, lecture.getId())).isTrue();
        assertThat(enrollmentRepository.existsByUserIdAndLectureId(user3, lecture.getId())).isTrue();
    }
}

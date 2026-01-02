package io.code.lecture.integration;

import io.code.lecture.application.enrollment.EnrollmentService;
import io.code.lecture.application.enrollment.dto.EnrollmentCommand;
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
@DisplayName("특강 신청 동시성 통합 테스트")
class EnrollmentConcurrencyTest {

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
    @DisplayName("동시에 40명이 신청하면 30명만 성공한다")
    void enroll_concurrent_40_users_only_30_success() throws InterruptedException {
        int threadCount = 40;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 1; i <= threadCount; i++) {
            final long userId = i;
            executorService.submit(() -> {
                try {
                    EnrollmentCommand command = new EnrollmentCommand(userId, lecture.getId());
                    enrollmentService.enroll(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        assertThat(successCount.get()).isEqualTo(30);
        assertThat(failCount.get()).isEqualTo(10);

        Lecture updatedLecture = lectureRepository.findById(lecture.getId()).orElseThrow();
        assertThat(updatedLecture.getCurrentEnrollment()).isEqualTo(30);
        assertThat(updatedLecture.canEnroll()).isFalse();
    }

    @Test
    @DisplayName("정원이 29명인 상태에서 2명이 동시 신청하면 1명만 성공한다")
    void enroll_concurrent_when_29_enrolled() throws InterruptedException {
        for (int i = 1; i <= 29; i++) {
            EnrollmentCommand command = new EnrollmentCommand((long) i, lecture.getId());
            enrollmentService.enroll(command);
        }

        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 30; i <= 31; i++) {
            final long userId = i;
            executorService.submit(() -> {
                try {
                    EnrollmentCommand command = new EnrollmentCommand(userId, lecture.getId());
                    enrollmentService.enroll(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        Lecture updatedLecture = lectureRepository.findById(lecture.getId()).orElseThrow();
        assertThat(updatedLecture.getCurrentEnrollment()).isEqualTo(30);
    }
}

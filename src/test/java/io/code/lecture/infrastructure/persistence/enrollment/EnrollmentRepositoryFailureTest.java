package io.code.lecture.infrastructure.persistence.enrollment;

import io.code.lecture.domain.enrollment.EnrollmentRepository;
import io.code.lecture.domain.lecture.Lecture;
import io.code.lecture.domain.lecture.LectureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("EnrollmentRepository 실패 케이스 테스트")
class EnrollmentRepositoryFailureTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LectureRepository lectureRepository;

    private Lecture lecture;

    @BeforeEach
    void setUp() {
        lecture = Lecture.builder()
                .title("테스트 특강")
                .instructor("강사")
                .lectureDate(LocalDate.now().plusDays(7))
                .build();
        lecture = lectureRepository.save(lecture);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 빈 리스트")
    void findByUserId_notFound() {
        var results = enrollmentRepository.findByUserId(999L);
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("신청자 없는 특강 카운트는 0")
    void countByLectureId_zero() {
        int count = enrollmentRepository.countByLectureId(lecture.getId());
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("신청하지 않은 조합은 false")
    void existsByUserIdAndLectureId_false() {
        boolean exists = enrollmentRepository.existsByUserIdAndLectureId(1L, lecture.getId());
        assertThat(exists).isFalse();
    }
}

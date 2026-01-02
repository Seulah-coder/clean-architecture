package io.code.lecture.infrastructure.persistence.enrollment;

import io.code.lecture.domain.enrollment.Enrollment;
import io.code.lecture.domain.enrollment.EnrollmentRepository;
import io.code.lecture.domain.lecture.Lecture;
import io.code.lecture.domain.lecture.LectureRepository;
import io.code.lecture.domain.user.User;
import io.code.lecture.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("EnrollmentRepository 통합 테스트")
class EnrollmentRepositoryImplTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private Lecture lecture1;
    private Lecture lecture2;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        user1 = User.builder()
                .name("홍길동")
                .email("hong@example.com")
                .build();
        user1 = userRepository.save(user1);

        user2 = User.builder()
                .name("김철수")
                .email("kim@example.com")
                .build();
        user2 = userRepository.save(user2);

        // 테스트용 특강 생성
        lecture1 = Lecture.builder()
                .title("클린 아키텍처")
                .instructor("김영한")
                .lectureDate(LocalDate.now().plusDays(7))
                .build();
        lecture1 = lectureRepository.save(lecture1);

        lecture2 = Lecture.builder()
                .title("TDD 실전")
                .instructor("박성철")
                .lectureDate(LocalDate.now().plusDays(14))
                .build();
        lecture2 = lectureRepository.save(lecture2);
    }

    @Test
    @DisplayName("수강 신청 저장 성공")
    void save_enrollment_success() {
        // given
        Enrollment enrollment = Enrollment.builder()
                .userId(user1.getId())
                .lectureId(lecture1.getId())
                .build();

        // when
        Enrollment saved = enrollmentRepository.save(enrollment);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(user1.getId());
        assertThat(saved.getLectureId()).isEqualTo(lecture1.getId());
        assertThat(saved.getEnrolledAt()).isNotNull();
    }

    @Test
    @DisplayName("중복 신청 존재 여부 확인 - 존재함")
    void existsByUserIdAndLectureId_true() {
        // given
        Enrollment enrollment = Enrollment.builder()
                .userId(user1.getId())
                .lectureId(lecture1.getId())
                .build();
        enrollmentRepository.save(enrollment);

        // when
        boolean exists = enrollmentRepository.existsByUserIdAndLectureId(
                user1.getId(), 
                lecture1.getId()
        );

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("중복 신청 존재 여부 확인 - 존재하지 않음")
    void existsByUserIdAndLectureId_false() {
        // when
        boolean exists = enrollmentRepository.existsByUserIdAndLectureId(
                user1.getId(), 
                lecture1.getId()
        );

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("사용자별 수강 신청 목록 조회")
    void findByUserId() {
        // given
        Enrollment enrollment1 = Enrollment.builder()
                .userId(user1.getId())
                .lectureId(lecture1.getId())
                .build();
        enrollmentRepository.save(enrollment1);

        Enrollment enrollment2 = Enrollment.builder()
                .userId(user1.getId())
                .lectureId(lecture2.getId())
                .build();
        enrollmentRepository.save(enrollment2);

        // user2도 하나 신청
        Enrollment enrollment3 = Enrollment.builder()
                .userId(user2.getId())
                .lectureId(lecture1.getId())
                .build();
        enrollmentRepository.save(enrollment3);

        // when
        List<Enrollment> user1Enrollments = enrollmentRepository.findByUserId(user1.getId());
        List<Enrollment> user2Enrollments = enrollmentRepository.findByUserId(user2.getId());

        // then
        assertThat(user1Enrollments).hasSize(2);
        assertThat(user1Enrollments)
                .extracting(Enrollment::getLectureId)
                .containsExactlyInAnyOrder(lecture1.getId(), lecture2.getId());

        assertThat(user2Enrollments).hasSize(1);
        assertThat(user2Enrollments.get(0).getLectureId()).isEqualTo(lecture1.getId());
    }

    @Test

    @DisplayName("사용자가 신청한 특강이 없으면 빈 리스트 반환")
    void findByUserId_empty() {
        // when
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(user1.getId());

        // then
        assertThat(enrollments).isEmpty();
    }

    @Test
    @DisplayName("같은 사용자가 다른 특강 여러 개 신청 가능")
    void save_multiple_enrollments_different_lectures() {
        // given & when
        Enrollment enrollment1 = enrollmentRepository.save(Enrollment.builder()
                .userId(user1.getId())
                .lectureId(lecture1.getId())
                .build());

        Enrollment enrollment2 = enrollmentRepository.save(Enrollment.builder()
                .userId(user1.getId())
                .lectureId(lecture2.getId())
                .build());

        // then
        assertThat(enrollment1.getId()).isNotNull();
        assertThat(enrollment2.getId()).isNotNull();
        assertThat(enrollment1.getId()).isNotEqualTo(enrollment2.getId());

        List<Enrollment> enrollments = enrollmentRepository.findByUserId(user1.getId());
        assertThat(enrollments).hasSize(2);
    }

    @Test
    @DisplayName("다른 사용자가 같은 특강 신청 가능")
    void save_multiple_enrollments_same_lecture() {
        // given & when
        Enrollment enrollment1 = enrollmentRepository.save(Enrollment.builder()
                .userId(user1.getId())
                .lectureId(lecture1.getId())
                .build());

        Enrollment enrollment2 = enrollmentRepository.save(Enrollment.builder()
                .userId(user2.getId())
                .lectureId(lecture1.getId())
                .build());

        // then
        assertThat(enrollment1.getId()).isNotNull();
        assertThat(enrollment2.getId()).isNotNull();

        int count = enrollmentRepository.countByLectureId(lecture1.getId());
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("신청 시간이 자동으로 기록된다")
    void enrollment_timestamp_auto_recorded() {
        // given
        Enrollment enrollment = Enrollment.builder()
                .userId(user1.getId())
                .lectureId(lecture1.getId())
                .build();

        // when
        Enrollment saved = enrollmentRepository.save(enrollment);

        // then
        assertThat(saved.getEnrolledAt()).isNotNull();
        assertThat(saved.getEnrolledAt())
                .isBeforeOrEqualTo(java.time.LocalDateTime.now());
    }
}
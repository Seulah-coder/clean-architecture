package io.code.lecture.application.enrollment;

import io.code.lecture.application.enrollment.dto.EnrollmentCommand;
import io.code.lecture.application.enrollment.dto.EnrollmentInfo;
import io.code.lecture.common.exception.DuplicateEnrollmentException;
import io.code.lecture.common.exception.EnrollmentFullException;
import io.code.lecture.common.exception.LectureNotFoundException;
import io.code.lecture.domain.enrollment.Enrollment;
import io.code.lecture.domain.enrollment.EnrollmentRepository;
import io.code.lecture.domain.lecture.Lecture;
import io.code.lecture.domain.lecture.LectureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnrollmentService 단위 테스트")
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private LectureRepository lectureRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private Lecture lecture;
    private EnrollmentCommand command;

    @BeforeEach
    void setUp() {
        lecture = Lecture.builder()
                .id(1L)
                .title("클린 아키텍처")
                .instructor("김영한")
                .lectureDate(LocalDate.now().plusDays(7))
                .build();

        command = new EnrollmentCommand(1L, 1L);
    }

    @Test
    @DisplayName("특강 신청 성공")
    void enroll_success() {
        // given
        given(enrollmentRepository.existsByUserIdAndLectureId(anyLong(), anyLong()))
                .willReturn(false);
        given(lectureRepository.findByIdWithLock(anyLong()))
                .willReturn(Optional.of(lecture));
        given(enrollmentRepository.save(any(Enrollment.class)))
                .willReturn(Enrollment.builder()
                        .id(1L)
                        .userId(1L)
                        .lectureId(1L)
                        .build());
        given(lectureRepository.save(any(Lecture.class)))
                .willReturn(lecture);

        // when
        EnrollmentInfo result = enrollmentService.enroll(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getLectureId()).isEqualTo(1L);
        assertThat(result.getLectureTitle()).isEqualTo("클린 아키텍처");

        verify(enrollmentRepository).existsByUserIdAndLectureId(1L, 1L);
        verify(lectureRepository).findByIdWithLock(1L);
        verify(enrollmentRepository).save(any(Enrollment.class));
        verify(lectureRepository).save(any(Lecture.class));
    }

    @Test
    @DisplayName("중복 신청 시 예외 발생")
    void enroll_fail_duplicate() {
        // given
        given(enrollmentRepository.existsByUserIdAndLectureId(anyLong(), anyLong()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> enrollmentService.enroll(command))
                .isInstanceOf(DuplicateEnrollmentException.class)
                .hasMessageContaining("이미 신청한 특강입니다");

        verify(enrollmentRepository).existsByUserIdAndLectureId(1L, 1L);
        verify(lectureRepository, never()).findByIdWithLock(anyLong());
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 특강 신청 시 예외 발생")
    void enroll_fail_lecture_not_found() {
        // given
        given(enrollmentRepository.existsByUserIdAndLectureId(anyLong(), anyLong()))
                .willReturn(false);
        given(lectureRepository.findByIdWithLock(anyLong()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> enrollmentService.enroll(command))
                .isInstanceOf(LectureNotFoundException.class)
                .hasMessageContaining("특강을 찾을 수 없습니다");

        verify(lectureRepository).findByIdWithLock(1L);
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("정원이 가득 찬 특강 신청 시 예외 발생")
    void enroll_fail_full_capacity() {
        // given
        Lecture fullLecture = Lecture.builder()
                .id(1L)
                .title("클린 아키텍처")
                .instructor("김영한")
                .lectureDate(LocalDate.now().plusDays(7))
                .currentEnrollment(30)
                .build();

        given(enrollmentRepository.existsByUserIdAndLectureId(anyLong(), anyLong()))
                .willReturn(false);
        given(lectureRepository.findByIdWithLock(anyLong()))
                .willReturn(Optional.of(fullLecture));

        // when & then
        assertThatThrownBy(() -> enrollmentService.enroll(command))
                .isInstanceOf(EnrollmentFullException.class)
                .hasMessageContaining("정원이 초과되었습니다");

        verify(enrollmentRepository, never()).save(any());
    }
}
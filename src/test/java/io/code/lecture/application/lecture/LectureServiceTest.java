package io.code.lecture.application.lecture;

import io.code.lecture.application.lecture.dto.LectureInfo;
import io.code.lecture.application.lecture.dto.LectureListResponse;
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
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LectureService 단위 테스트")
class LectureServiceTest {

    @Mock
    private LectureRepository lectureRepository;

    @InjectMocks
    private LectureService lectureService;

    private LocalDate targetDate;

    @BeforeEach
    void setUp() {
        targetDate = LocalDate.of(2024, 6, 1);
    }

    @Test
    @DisplayName("날짜별 신청 가능한 특강 목록 조회 성공")
    void getAvailableLectures_success() {
        // given
        List<Lecture> lectures = Arrays.asList(
                createLecture(1L, "클린 아키텍처", 0),
                createLecture(2L, "TDD 실전", 15),
                createLecture(3L, "DDD 입문", 29)
        );

        given(lectureRepository.findByLectureDate(any(LocalDate.class)))
                .willReturn(lectures);

        // when
        LectureListResponse response = lectureService.getAvailableLectures(targetDate);

        // then
        assertThat(response.getLectures()).hasSize(3);
        assertThat(response.getLectures())
                .extracting(LectureInfo::getTitle)
                .containsExactly("클린 아키텍처", "TDD 실전", "DDD 입문");

        verify(lectureRepository).findByLectureDate(targetDate);
    }

    @Test
    @DisplayName("정원이 가득 찬 특강은 목록에서 제외된다")
    void getAvailableLectures_exclude_full() {
        // given
        List<Lecture> lectures = Arrays.asList(
                createLecture(1L, "클린 아키텍처", 15),
                createLecture(2L, "TDD 실전", 30),  // 정원 가득참
                createLecture(3L, "DDD 입문", 29)
        );

        given(lectureRepository.findByLectureDate(any(LocalDate.class)))
                .willReturn(lectures);

        // when
        LectureListResponse response = lectureService.getAvailableLectures(targetDate);

        // then
        assertThat(response.getLectures()).hasSize(2);
        assertThat(response.getLectures())
                .extracting(LectureInfo::getTitle)
                .containsExactly("클린 아키텍처", "DDD 입문");
    }

    private Lecture createLecture(Long id, String title, int currentEnrollment) {
        return Lecture.builder()
                .id(id)
                .title(title)
                .instructor("강사")
                .lectureDate(targetDate)
                .currentEnrollment(currentEnrollment)
                .build();
    }
}
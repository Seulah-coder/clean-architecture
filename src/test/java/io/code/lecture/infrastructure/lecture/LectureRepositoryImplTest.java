package io.code.lecture.infrastructure.persistence.lecture;

import io.code.lecture.domain.lecture.Lecture;
import io.code.lecture.domain.lecture.LectureRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("LectureRepository 통합 테스트")
class LectureRepositoryImplTest {

    @Autowired
    private LectureRepository lectureRepository;

    @Test
    @DisplayName("특강 저장 및 조회")
    void save_and_find() {
        // given
        Lecture lecture = Lecture.builder()
                .title("클린 아키텍처")
                .instructor("김영한")
                .lectureDate(LocalDate.now().plusDays(7))
                .build();

        // when
        Lecture saved = lectureRepository.save(lecture);
        Optional<Lecture> found = lectureRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("클린 아키텍처");
        assertThat(found.get().getInstructor()).isEqualTo("김영한");
    }

    @Test
    @DisplayName("날짜별 특강 목록 조회")
    void findByLectureDate() {
        // given
        LocalDate targetDate = LocalDate.now().plusDays(7);

        Lecture lecture1 = createAndSaveLecture("강의1", targetDate);
        Lecture lecture2 = createAndSaveLecture("강의2", targetDate);
        Lecture lecture3 = createAndSaveLecture("강의3", targetDate.plusDays(1));

        // when
        List<Lecture> lectures = lectureRepository.findByLectureDate(targetDate);

        // then
        assertThat(lectures).hasSize(2);
        assertThat(lectures)
                .extracting(Lecture::getTitle)
                .containsExactlyInAnyOrder("강의1", "강의2");
    }

    private Lecture createAndSaveLecture(String title, LocalDate date) {
        Lecture lecture = Lecture.builder()
                .title(title)
                .instructor("강사")
                .lectureDate(date)
                .build();
        return lectureRepository.save(lecture);
    }
}
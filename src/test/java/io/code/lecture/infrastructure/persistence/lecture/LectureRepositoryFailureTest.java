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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("LectureRepository 실패 케이스 테스트")
class LectureRepositoryFailureTest {

    @Autowired
    private LectureRepository lectureRepository;

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional")
    void findById_notFound() {
        Optional<Lecture> result = lectureRepository.findById(999L);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 날짜로 조회 시 빈 리스트")
    void findByLectureDate_noResults() {
        var results = lectureRepository.findByLectureDate(LocalDate.now().plusYears(10));
        assertThat(results).isEmpty();
    }
}

package io.code.lecture.domain.lecture;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LectureRepository {
    Optional<Lecture> findById(Long id);
    List<Lecture> findByLectureDate(LocalDate date);
    Lecture save(Lecture lecture);
    Optional<Lecture> findByIdWithLock(Long id); // 비관적 락
}
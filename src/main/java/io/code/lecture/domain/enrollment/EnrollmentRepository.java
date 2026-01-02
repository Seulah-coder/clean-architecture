package io.code.lecture.domain.enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {
    Enrollment save(Enrollment enrollment);
    boolean existsByUserIdAndLectureId(Long userId, Long lectureId);
    List<Enrollment> findByUserId(Long userId);
    int countByLectureId(Long lectureId);
}
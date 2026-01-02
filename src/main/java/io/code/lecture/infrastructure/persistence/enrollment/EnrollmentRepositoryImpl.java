package io.code.lecture.infrastructure.persistence.enrollment;

import io.code.lecture.domain.enrollment.Enrollment;
import io.code.lecture.domain.enrollment.EnrollmentRepository;
import io.code.lecture.common.exception.DuplicateEnrollmentException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentRepository {
    
    private final EnrollmentJpaRepository jpaRepository;
    
    @Override
    public Enrollment save(Enrollment enrollment) {
        try {
            EnrollmentEntity entity = EnrollmentEntity.from(enrollment);
            EnrollmentEntity saved = jpaRepository.save(entity);
            return saved.toDomain();
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEnrollmentException("이미 신청한 특강입니다.");
        }
    }
    
    @Override
    public boolean existsByUserIdAndLectureId(Long userId, Long lectureId) {
        return jpaRepository.existsByUserIdAndLectureId(userId, lectureId);
    }
    
    @Override
    public List<Enrollment> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
            .map(EnrollmentEntity::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Enrollment> findByLectureId(Long lectureId) {
        return jpaRepository.findByLectureId(lectureId).stream()
            .map(EnrollmentEntity::toDomain)
            .collect(Collectors.toList());
    }
}

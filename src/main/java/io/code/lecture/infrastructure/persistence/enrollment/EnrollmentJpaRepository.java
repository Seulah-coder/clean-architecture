package io.code.lecture.infrastructure.persistence.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentEntity, Long> {
    
    // 중복 신청 체크
    boolean existsByUserIdAndLectureId(Long userId, Long lectureId);
    
    // 특정 사용자의 신청 목록
    List<EnrollmentEntity> findByUserId(Long userId);
    
    // 특정 강의의 신청 목록
    List<EnrollmentEntity> findByLectureId(Long lectureId);
    
}
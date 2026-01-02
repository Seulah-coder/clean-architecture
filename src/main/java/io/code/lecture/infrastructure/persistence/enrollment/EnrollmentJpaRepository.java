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
    
    // 특정 강의의 신청 수 (현재는 lectures 테이블의 current_enrollment 사용)
    @Query("SELECT COUNT(e) FROM EnrollmentEntity e WHERE e.lectureId = :lectureId")
    int countByLectureId(@Param("lectureId") Long lectureId);
    
    // 사용자의 특정 강의 신청 여부
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
           "FROM EnrollmentEntity e " +
           "WHERE e.userId = :userId AND e.lectureId = :lectureId")
    boolean isEnrolled(@Param("userId") Long userId, @Param("lectureId") Long lectureId);
}
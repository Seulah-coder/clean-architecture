package io.code.lecture.infrastructure.persistence.lecture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LectureJpaRepository extends JpaRepository<LectureEntity, Long> {
    
    // 날짜별 특강 조회
    List<LectureEntity> findByLectureDate(LocalDate lectureDate);
    
    // 신청 가능한 특강만 조회 (날짜 + 정원 미달)
    @Query("SELECT l FROM LectureEntity l " +
           "WHERE l.lectureDate = :date " +
           "AND l.currentEnrollment < l.maxCapacity")
    List<LectureEntity> findAvailableLecturesByDate(@Param("date") LocalDate date);
    
    // 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM LectureEntity l WHERE l.id = :id")
    Optional<LectureEntity> findByIdWithPessimisticLock(@Param("id") Long id);
    
    // 낙관적 락
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT l FROM LectureEntity l WHERE l.id = :id")
    Optional<LectureEntity> findByIdWithOptimisticLock(@Param("id") Long id);
}
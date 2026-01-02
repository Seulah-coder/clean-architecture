package io.code.lecture.infrastructure.persistence.lecture;

import io.code.lecture.domain.lecture.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureRepository {
    
    private final LectureJpaRepository jpaRepository;
    
    @Override
    public Optional<Lecture> findById(Long id) {
        return jpaRepository.findById(id)
            .map(LectureEntity::toDomain);
    }
    
    @Override
    public List<Lecture> findByLectureDate(LocalDate date) {
        return jpaRepository.findByLectureDate(date).stream()
            .map(LectureEntity::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Lecture save(Lecture lecture) {
        LectureEntity entity = LectureEntity.from(lecture);
        LectureEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public Optional<Lecture> findByIdWithLock(Long id) {
        return jpaRepository.findByIdWithPessimisticLock(id)
            .map(LectureEntity::toDomain);
    }
}
package io.code.lecture.infrastructure.persistence.lecture;

import io.code.lecture.domain.lecture.Lecture;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lectures")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LectureEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, length = 100)
    private String instructor;
    
    @Column(name = "lecture_date", nullable = false)
    private LocalDate lectureDate;
    
    @Builder.Default
    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity = 30;
    
    @Builder.Default
    @Column(name = "current_enrollment", nullable = false)
    private Integer currentEnrollment = 0;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (maxCapacity == null) {
            maxCapacity = 30;
        }
        if (currentEnrollment == null) {
            currentEnrollment = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public Lecture toDomain() {
        return Lecture.builder()
            .id(this.id)
            .title(this.title)
            .instructor(this.instructor)
            .lectureDate(this.lectureDate)
            .maxCapacity(this.maxCapacity)
            .currentEnrollment(this.currentEnrollment)
            .createdAt(this.createdAt)
            .build();
    }
    
    public static LectureEntity from(Lecture lecture) {
        return LectureEntity.builder()
            .id(lecture.getId())
            .title(lecture.getTitle())
            .instructor(lecture.getInstructor())
            .lectureDate(lecture.getLectureDate())
            .maxCapacity(lecture.getMaxCapacity())
            .currentEnrollment(lecture.getCurrentEnrollment())
            .build();
    }
}

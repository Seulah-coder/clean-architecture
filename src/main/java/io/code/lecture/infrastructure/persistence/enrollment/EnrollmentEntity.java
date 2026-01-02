package io.code.lecture.infrastructure.persistence.enrollment;

import io.code.lecture.domain.enrollment.Enrollment;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "enrollments",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_lecture", columnNames = {"user_id", "lecture_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class EnrollmentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "lecture_id", nullable = false)
    private Long lectureId;
    
    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private LocalDateTime enrolledAt;
    
    @PrePersist
    protected void onCreate() {
        if (enrolledAt == null) {
            enrolledAt = LocalDateTime.now();
        }
    }
    
    public Enrollment toDomain() {
        return Enrollment.builder()
            .id(this.id)
            .userId(this.userId)
            .lectureId(this.lectureId)
            .enrolledAt(this.enrolledAt)
            .build();
    }
    
    public static EnrollmentEntity from(Enrollment enrollment) {
        return EnrollmentEntity.builder()
            .id(enrollment.getId())
            .userId(enrollment.getUserId())
            .lectureId(enrollment.getLectureId())
            .enrolledAt(enrollment.getEnrolledAt() != null ? enrollment.getEnrolledAt() : LocalDateTime.now())
            .build();
    }
}

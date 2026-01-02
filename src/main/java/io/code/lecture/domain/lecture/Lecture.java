package io.code.lecture.domain.lecture;

import io.code.lecture.common.exception.EnrollmentFullException;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class Lecture {
    private Long id;
    private String title;
    private String instructor;
    private LocalDate lectureDate;
    
    @Builder.Default
    private Integer maxCapacity = 30;
    
    @Builder.Default
    private Integer currentEnrollment = 0;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public boolean canEnroll() {
        return currentEnrollment < maxCapacity;
    }
    
    public void incrementEnrollment() {
        if (currentEnrollment >= maxCapacity) {
            throw new EnrollmentFullException("특강 정원이 초과되었습니다.");
        }
        currentEnrollment++;
    }
    
    public int getAvailableSeats() {
        return maxCapacity - currentEnrollment;
    }
}

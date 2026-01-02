package io.code.lecture.application.lecture.dto;
import io.code.lecture.domain.lecture.Lecture;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class LectureInfo {
    private final Long id;
    private final String title;
    private final String instructor;
    private final LocalDate lectureDate;
    private final int availableSeats;
    
    private LectureInfo(Long id, String title, String instructor, LocalDate lectureDate, int availableSeats) {
        this.id = id;
        this.title = title;
        this.instructor = instructor;
        this.lectureDate = lectureDate;
        this.availableSeats = availableSeats;
    }
    
    public static LectureInfo from(Lecture lecture) {
        return new LectureInfo(
            lecture.getId(),
            lecture.getTitle(),
            lecture.getInstructor(),
            lecture.getLectureDate(),
            lecture.getAvailableSeats()
        );
    }
}

package io.code.lecture.application.lecture.dto;
import lombok.Getter;
import java.util.List;

@Getter
public class LectureListResponse {
    private final List<LectureInfo> lectures;
    public LectureListResponse(List<LectureInfo> lectures) {
        this.lectures = lectures;
    }
}

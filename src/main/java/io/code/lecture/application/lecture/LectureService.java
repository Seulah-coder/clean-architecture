package io.code.lecture.application.lecture;

import io.code.lecture.domain.lecture.*;
import io.code.lecture.application.lecture.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureService {
    
    private final LectureRepository lectureRepository;
    
    /**
     * 날짜별 신청 가능한 특강 목록 조회
     */
    @Transactional(readOnly = true)
    public LectureListResponse getAvailableLectures(LocalDate date) {
        List<Lecture> lectures = lectureRepository.findByLectureDate(date);
        
        List<LectureInfo> lectureInfos = lectures.stream()
            .filter(Lecture::canEnroll)  // 신청 가능한 특강만 필터링
            .map(LectureInfo::from)
            .collect(Collectors.toList());
        
        return new LectureListResponse(lectureInfos);
    }
}
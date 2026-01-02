package io.code.lecture.presentation.api.enrollment;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.code.lecture.application.enrollment.EnrollmentService;
import io.code.lecture.application.enrollment.dto.EnrollmentCommand;
import io.code.lecture.common.exception.DuplicateEnrollmentException;
import io.code.lecture.common.exception.EnrollmentFullException;
import io.code.lecture.common.exception.LectureNotFoundException;
import io.code.lecture.presentation.api.enrollment.request.EnrollmentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
@DisplayName("EnrollmentController 실패 케이스 테스트")
class EnrollmentControllerFailureTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnrollmentService enrollmentService;

    @Test
    @DisplayName("중복 신청 시 400")
    void enroll_duplicate() throws Exception {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setUserId(1L);
        request.setLectureId(1L);

        given(enrollmentService.enroll(any(EnrollmentCommand.class)))
                .willThrow(new DuplicateEnrollmentException("이미 신청한 특강입니다"));

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정원 초과 시 400")
    void enroll_full() throws Exception {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setUserId(1L);
        request.setLectureId(1L);

        given(enrollmentService.enroll(any(EnrollmentCommand.class)))
                .willThrow(new EnrollmentFullException("정원이 초과되었습니다"));

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("특강 없음 시 404")
    void enroll_notFound() throws Exception {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setUserId(1L);
        request.setLectureId(999L);

        given(enrollmentService.enroll(any(EnrollmentCommand.class)))
                .willThrow(new LectureNotFoundException("특강을 찾을 수 없습니다"));

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}

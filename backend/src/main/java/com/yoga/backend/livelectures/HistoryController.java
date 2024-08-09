package com.yoga.backend.livelectures;

import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.livelectures.dto.LectureHistoryDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage/course-history")
public class HistoryController {

    private final JwtUtil jwtUtil;
    private final HistoryService hsitoryService;

    public HistoryController(JwtUtil jwtUtil,
        HistoryService hsitoryService) {
        this.jwtUtil = jwtUtil;
        this.hsitoryService = hsitoryService;

    }


    /**
     * 수강 내역 처리
     *
     * @param token JWT 토큰
     * @return 수강 내역 페이지에 대한 응답 DTO를 포함한 ResponseEntity 객체
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHomeData(
        @RequestHeader("Authorization") String token) {
        int userId = jwtUtil.getUserIdFromToken(token);
        List<LectureHistoryDto> history = hsitoryService.getHistory(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "내 화상 강의 할 일 조회 성공");
        response.put("data", history);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
package com.yoga.backend.mypage.mylivelecture;

import com.yoga.backend.common.entity.MyLiveLecture;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * 나의 실시간 강의 리포지토리 인터페이스
 * 데이터베이스와의 상호작용을 정의
 */
public interface MyLiveLectureRepository extends JpaRepository<MyLiveLecture, Long> {
    /**
     * 특정 사용자 ID에 대한 나의 실시간 강의 목록을 조회
     * @param userId 사용자 ID
     * @return 나의 실시간 강의 리스트
     */
    List<MyLiveLecture> findByUserId(Long userId); // 사용자 ID로 화상 강의 목록 조회
}
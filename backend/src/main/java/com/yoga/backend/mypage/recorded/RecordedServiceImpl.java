package com.yoga.backend.mypage.recorded;

import com.yoga.backend.common.awsS3.S3Service;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLecture;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLectureChapter;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLectureLike;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.mypage.recorded.dto.ChapterDto;
import com.yoga.backend.mypage.recorded.dto.DeleteDto;
import com.yoga.backend.mypage.recorded.dto.LectureDto;
import com.yoga.backend.mypage.recorded.repository.AllRecordedLecturesRepository;
import com.yoga.backend.mypage.recorded.repository.MyLikeLectureListRepository;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureLikeRepository;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureListRepository;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @TODO 업데이트 하고 안쓰는 애들 삭제 시 잘 생성된것도 삭제됨.
 * <p>
 * 녹화 강의 관련 비즈니스 로직을 처리하는 서비스 구현 클래스. 강의 목록 조회, 강의 생성, 수정, 삭제 및 좋아요 기능을 제공.
 */
@Slf4j
@Service
public class RecordedServiceImpl implements RecordedService {

    public static final long URL_EXPIRATION_SECONDS = 86400;
    private static final String S3_BASE_URL = "https://yoga-navi.s3.ap-northeast-2.amazonaws.com/";

    private final S3Service s3Service;
    private UsersRepository usersRepository;
    private final RecordedLectureRepository recordedLectureRepository;
    private final RecordedLectureListRepository recordedLectureListRepository;
    private final MyLikeLectureListRepository myLikeLectureListRepository;
    private final RecordedLectureLikeRepository lectureLikeRepository;
    private final AllRecordedLecturesRepository allRecordedLecturesRepository;

    @Autowired
    public RecordedServiceImpl(S3Service s3Service,
        UsersRepository usersRepository,
        RecordedLectureRepository recordedLectureRepository,
        RecordedLectureListRepository recordedLectureListRepository,
        MyLikeLectureListRepository myLikeLectureListRepository,
        RecordedLectureLikeRepository lectureLikeRepository,
        AllRecordedLecturesRepository allRecordedLecturesRepository
    ) {
        this.s3Service = s3Service;
        this.usersRepository = usersRepository;
        this.recordedLectureRepository = recordedLectureRepository;
        this.recordedLectureListRepository = recordedLectureListRepository;
        this.myLikeLectureListRepository = myLikeLectureListRepository;
        this.lectureLikeRepository = lectureLikeRepository;
        this.allRecordedLecturesRepository = allRecordedLecturesRepository;
    }

    /**
     * 사용자가 업로드한 강의 목록을 조회
     *
     * @param userId 사용자 ID
     * @return 사용자가 업로드한 강의 목록 (LectureDto 리스트)
     */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<LectureDto> getMyLectures(int userId) {
        List<LectureDto> lectures = recordedLectureListRepository.findAllLectures(userId);
        return applyPresignedUrls(lectures);
    }

    /**
     * 사용자가 좋아요한 강의 목록을 조회
     *
     * @param userId 사용자 ID
     * @return 사용자가 좋아요한 강의 목록 (LectureDto 리스트)
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<LectureDto> getLikeLectures(int userId) {
        List<LectureDto> lectures = myLikeLectureListRepository.findMyLikedLectures(userId);
        return generatePresignedUrlsLike(lectures);
    }

    /**
     * 새로운 강의를 업로드
     *
     * @param lectureDto 사용자가 저장할 강의 정보
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void saveLecture(LectureDto lectureDto) {
        Users user = usersRepository.findById(lectureDto.getUserId())
            .orElseThrow(() -> new RuntimeException("사용자 찾을 수 없음"));

        RecordedLecture lecture = new RecordedLecture();
        lecture.setUser(user);
        lecture.setTitle(lectureDto.getRecordTitle());
        lecture.setContent(lectureDto.getRecordContent());
        lecture.setThumbnail(lectureDto.getRecordThumbnail());
        lecture.setThumbnailSmall(lectureDto.getRecordThumbnailSmall());

        List<RecordedLectureChapter> chapters = new ArrayList<>();
        for (ChapterDto chapterDto : lectureDto.getRecordedLectureChapters()) {
            RecordedLectureChapter chapter = new RecordedLectureChapter();
            chapter.setTitle(chapterDto.getChapterTitle());
            chapter.setDescription(chapterDto.getChapterDescription());
            chapter.setVideoUrl(chapterDto.getRecordVideo());
            chapter.setLecture(lecture);
            chapter.setChapterNumber(chapterDto.getChapterNumber());
            chapters.add(chapter);
        }
        lecture.setChapters(chapters);
        recordedLectureRepository.save(lecture);
    }

    /**
     * 강의 내용 상세 정보를 조회
     *
     * @param recordedId 강의 ID
     * @param userId     사용자 ID
     * @return 강의 상세 정보
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public LectureDto getLectureDetails(Long recordedId, int userId) {
        RecordedLecture lecture = recordedLectureRepository.findById(recordedId)
            .orElseThrow(() -> new RuntimeException("강의 찾을 수 없음"));

        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자 찾을 수 없음"));

        LectureDto dto = convertToDto(lecture);
        dto.setLikeCount(lecture.getLikeCount());
        dto.setMyLike(lectureLikeRepository.existsByLectureAndUser(lecture, user));

        // Presigned URL 생성 및 적용
        return applyPresignedUrls(Collections.singletonList(dto)).get(0);
    }

    /**
     * 강의 정보를 업데이트
     *
     * @param lectureDto 업데이트할 강의 정보
     * @return 업데이트 성공 여부
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean updateLecture(LectureDto lectureDto) {
        try {
            // 데이터베이스에서 강의 정보를 조회
            RecordedLecture lecture = recordedLectureRepository.findById(lectureDto.getRecordedId())
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));

            // 사용자 권한 확인
            if (lecture.getUser().getId() != lectureDto.getUserId()) {
                throw new RuntimeException("해당 강의를 수정할 권한이 없습니다.");
            }

            // 강의 기본 정보 업데이트 (제목, 내용, 썸네일)
            updateLectureDetails(lecture, lectureDto);

            // 강의 챕터 정보 업데이트 (추가, 수정, 삭제)
            updateChapters(lecture, lectureDto.getRecordedLectureChapters());

            // 변경된 강의 정보를 데이터베이스에 저장
            recordedLectureRepository.save(lecture);
            return true;
        } catch (Exception e) {
            log.error("강의 수정 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 강의의 기본 정보를 업데이트
     *
     * @param lecture    기존 강의 엔티티
     * @param lectureDto 새로운 강의 정보가 담긴 DTO
     */
    private void updateLectureDetails(RecordedLecture lecture, LectureDto lectureDto) {
        // 강의 제목 업데이트 (변경된 경우)
        if (!lecture.getTitle().equals(lectureDto.getRecordTitle())) {
            lecture.setTitle(lectureDto.getRecordTitle());
            log.info("강의 제목 업데이트: {}", lectureDto.getRecordTitle());
        }

        // 강의 내용 업데이트 (변경된 경우)
        if (!lecture.getContent().equals(lectureDto.getRecordContent())) {
            lecture.setContent(lectureDto.getRecordContent());
            log.info("강의 내용 업데이트");
        }

        // 강의 썸네일 업데이트 (변경된 경우)
        if (!lecture.getThumbnail().equals(lectureDto.getRecordThumbnail())) {
            // 기존 썸네일 삭제
            s3Service.deleteFile(lecture.getThumbnail());
            // 새 썸네일 설정
            lecture.setThumbnail(lectureDto.getRecordThumbnail());
            log.info("강의 썸네일 업데이트: {}", lectureDto.getRecordThumbnail());
        }

        // 작은 썸네일 업데이트 (변경된 경우)
        if (!lecture.getThumbnailSmall().equals(lectureDto.getRecordThumbnailSmall())) {
            // 기존 저용량 썸네일 삭제
            s3Service.deleteFile(lecture.getThumbnailSmall());
            // 새 저용량 썸네일 설정
            lecture.setThumbnailSmall(lectureDto.getRecordThumbnailSmall());
            log.info("강의 소형 썸네일 업데이트: {}", lectureDto.getRecordThumbnail());
        }
    }

    /**
     * 강의의 챕터 정보를 업데이트하는 메서드
     *
     * @param lecture     기존 강의 엔티티
     * @param chapterDtos 새로운 챕터 정보가 담긴 DTO 리스트
     */
    private void updateChapters(RecordedLecture lecture, List<ChapterDto> chapterDtos) {
        // 현재 강의의 모든 챕터 ID를 Set으로 저장
        Set<Long> existingChapterIds = lecture.getChapters().stream()
            .map(RecordedLectureChapter::getId)
            .collect(Collectors.toSet());

        // 프론트에서 보낸 챕터 ID를 Set으로 저장
        Set<Long> receivedChapterIds = chapterDtos.stream()
            .map(ChapterDto::getId)
            .filter(id -> id != 0) // 새로 추가된 챕터는 ID가 0이므로 제외
            .collect(Collectors.toSet());

        // 삭제될 챕터 ID (기존 ID 중 받지 않은 ID)
        Set<Long> chapterIdsToDelete = new HashSet<>(existingChapterIds);
        chapterIdsToDelete.removeAll(receivedChapterIds);

        // 챕터 삭제 처리
        for (Long chapterId : chapterIdsToDelete) {
            RecordedLectureChapter chapterToDelete = lecture.getChapters().stream()
                .filter(chapter -> chapter.getId().equals(chapterId))
                .findFirst()
                .orElse(null);

            if (chapterToDelete != null) {
                if (chapterToDelete.getVideoUrl() != null && !chapterToDelete.getVideoUrl()
                    .isEmpty()) {
                    s3Service.deleteFile(chapterToDelete.getVideoUrl());
                }
                lecture.getChapters().remove(chapterToDelete);
                log.info("챕터 삭제: {}", chapterId);
            }
        }

        // 챕터 업데이트 및 새 챕터 추가
        for (ChapterDto chapterDto : chapterDtos) {
            if (chapterDto.getId() != 0) {
                // 기존 챕터 업데이트
                RecordedLectureChapter chapter = lecture.getChapters().stream()
                    .filter(c -> c.getId() == chapterDto.getId())
                    .findFirst()
                    .orElse(null);

                if (chapter != null) {
                    updateChapter(chapter, chapterDto);
                }
            } else {
                // 새 챕터 추가
                RecordedLectureChapter newChapter = createChapter(chapterDto, lecture);
                lecture.getChapters().add(newChapter);
                log.info("새 챕터 추가: {}", newChapter.getTitle());
            }
        }
    }

    /**
     * 개별 챕터 정보를 업데이트
     *
     * @param chapter    기존 챕터 엔티티
     * @param chapterDto 새로운 챕터 정보가 담긴 DTO
     */
    private void updateChapter(RecordedLectureChapter chapter, ChapterDto chapterDto) {
        // 챕터 제목 업데이트 (변경된 경우에만)
        if (!Objects.equals(chapter.getTitle(), chapterDto.getChapterTitle())) {
            chapter.setTitle(chapterDto.getChapterTitle());
            log.info("챕터 제목 업데이트: {}", chapter.getId());
        }

        // 챕터 설명 업데이트 (변경된 경우에만)
        if (!Objects.equals(chapter.getDescription(), chapterDto.getChapterDescription())) {
            chapter.setDescription(chapterDto.getChapterDescription());
            log.info("챕터 설명 업데이트: {}", chapter.getId());
        }

        // 챕터 번호 업데이트 (변경된 경우에만)
        if (chapter.getChapterNumber() != chapterDto.getChapterNumber()) {
            chapter.setChapterNumber(chapterDto.getChapterNumber());
            log.info("챕터 번호 업데이트: {}", chapter.getId());
        }

        // 비디오 URL 업데이트
        String existingUrl = chapter.getVideoUrl();
        String newUrl = chapterDto.getRecordVideo();

        if (!Objects.equals(extractS3Key(existingUrl), extractS3Key(newUrl))) {
            if (newUrl != null && !newUrl.isEmpty()) {
                // 새 비디오 URL이 제공된 경우
                if (existingUrl != null && !existingUrl.isEmpty()) {
                    s3Service.deleteFile(existingUrl);
                }
                chapter.setVideoUrl(newUrl);
                log.info("챕터 비디오 URL 업데이트: {} -> {}", chapter.getId(), newUrl);
            } else if (existingUrl != null && !existingUrl.isEmpty()) {
                // 새 URL이 없고 기존 URL이 있는 경우 (비디오 삭제)
                s3Service.deleteFile(existingUrl);
                chapter.setVideoUrl(null);
                log.info("챕터 비디오 삭제: {}", chapter.getId());
            }
        }
    }

    private RecordedLectureChapter createChapter(ChapterDto chapterDto, RecordedLecture lecture) {
        RecordedLectureChapter chapter = new RecordedLectureChapter();
        chapter.setTitle(chapterDto.getChapterTitle());
        chapter.setDescription(chapterDto.getChapterDescription());
        chapter.setChapterNumber(chapterDto.getChapterNumber());
        chapter.setVideoUrl(chapterDto.getRecordVideo());
        chapter.setLecture(lecture);
        return chapter;
    }


    /**
     * 강의를 삭제
     *
     * @param deleteDto 삭제할 강의 ID 목록
     * @param userId    사용자 ID
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteLectures(DeleteDto deleteDto, int userId) {
        log.info("이 id를 가진 강의들을 삭제: {}", deleteDto);

        if (deleteDto.getLectureIds() == null || deleteDto.getLectureIds().isEmpty()) {
            log.warn("삭제를 위해서는 강의 id가 필요함. 강의 id가 존재하지 않음");
            throw new RuntimeException("삭제할 강의가 지정되지 않았습니다.");
        }

        try {
            List<RecordedLecture> lectures = recordedLectureRepository.findAllByIdCustom(
                deleteDto.getLectureIds());

            if (lectures.isEmpty()) {
                log.warn("삭제할 강의가 존재하지 않음");
                throw new RuntimeException("삭제할 강의를 찾을 수 없습니다.");
            }

            List<Long> deletedLectureIds = new ArrayList<>();
            List<Long> notFoundLectureIds = new ArrayList<>(deleteDto.getLectureIds());

            for (RecordedLecture lecture : lectures) {
                if (lecture.getUser().getId() != userId) {
                    log.error("사용자 {} 는 이 강의를 삭제할 권한이 없음 {}", userId, lecture.getId());
                    throw new RuntimeException("강의 ID " + lecture.getId() + "에 대한 삭제 권한이 없습니다.");
                }

                log.info("강의의 s3파일 삭제중: {}", lecture.getId());
                deleteS3Files(lecture);

                log.info("db에서 강의 삭제중: {}", lecture.getId());
                recordedLectureRepository.delete(lecture);

                deletedLectureIds.add(lecture.getId());
                notFoundLectureIds.remove(lecture.getId());
            }

            log.info("강의 삭제 완료: {}", deletedLectureIds);
            if (!notFoundLectureIds.isEmpty()) {
                log.warn("강의를 찾을 수 없음: {}", notFoundLectureIds);
            }
        } catch (Exception e) {
            log.error("강의 삭제중 에러 발생", e);
            throw new RuntimeException("강의 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void deleteS3Files(RecordedLecture lecture) {
        try {
            if (lecture.getThumbnail() != null && !lecture.getThumbnail().isEmpty()) {
                log.info("강의 썸네일 삭제 {}: {}", lecture.getId(), lecture.getThumbnail());
                s3Service.deleteFile(lecture.getThumbnail());
            }

            if (lecture.getThumbnailSmall() != null && !lecture.getThumbnailSmall().isEmpty()) {
                s3Service.deleteFile(lecture.getThumbnailSmall());
            }

            for (RecordedLectureChapter chapter : lecture.getChapters()) {
                if (chapter.getVideoUrl() != null && !chapter.getVideoUrl().isEmpty()) {
                    log.info("챕터의 비디오 삭제 {} 강의 번호 {}: {}",
                        chapter.getId(), lecture.getId(), chapter.getVideoUrl());
                    s3Service.deleteFile(chapter.getVideoUrl());
                }
            }
        } catch (Exception e) {
            log.error("강의의 s3파일 삭제중 에러 발생: {}", lecture.getId(), e);
        }
    }

    /**
     * 강의에 대한 좋아요를 토글
     *
     * @param recordedId 강의 ID
     * @param userId     사용자 ID
     * @return 좋아요 상태 (true: 좋아요 추가, false: 좋아요 취소)
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean toggleLike(Long recordedId, int userId) {
        RecordedLecture lecture = recordedLectureRepository.findById(recordedId)
            .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        boolean exists = lectureLikeRepository.existsByLectureAndUser(lecture, user);
        if (exists) {
            lectureLikeRepository.deleteByLectureAndUser(lecture, user);
            lecture.decrementLikeCount();
            log.info("강의 {}의 좋아요가 사용자 {}에 의해 취소됨", recordedId, userId);
            return false;
        } else {
            RecordedLectureLike like = new RecordedLectureLike();
            like.setLecture(lecture);
            like.setUser(user);
            lectureLikeRepository.save(like);
            lecture.incrementLikeCount();
            log.info("강의 {}의 좋아요가 사용자 {}에 의해 추가됨", recordedId, userId);
            return true;
        }
    }

    @Override
    public List<LectureDto> getAllLectures(int userId, int page, int size, String sort) {
        List<LectureDto> lectures = allRecordedLecturesRepository.findAllLectures(userId, page,
            size, sort);
        return applyPresignedUrls(lectures);
    }

    @Transactional(readOnly = true)
    public List<LectureDto> searchLectures(int userId, String keyword, String sort, int page, int size, boolean searchTitle, boolean searchContent) {
        List<LectureDto> lectures = recordedLectureListRepository.searchLectures(userId, keyword, sort, page, size, searchTitle, searchContent);
        return applyPresignedUrls(lectures);
    }

    private LectureDto convertToDto(RecordedLecture lecture) {
        LectureDto dto = new LectureDto();
        dto.setRecordedId(lecture.getId());
        dto.setUserId(lecture.getUser().getId());
        dto.setRecordTitle(lecture.getTitle());
        dto.setRecordContent(lecture.getContent());
        dto.setRecordThumbnail(lecture.getThumbnail());
        dto.setRecordThumbnailSmall(lecture.getThumbnailSmall());
        dto.setLikeCount(lecture.getLikeCount());
        dto.setMyLike(false); // 이 값은 나중에 설정됩니다.

        List<ChapterDto> chapterDtos = new ArrayList<>();
        for (RecordedLectureChapter chapter : lecture.getChapters()) {
            ChapterDto chapterDto = new ChapterDto();
            chapterDto.setId(chapter.getId());
            chapterDto.setChapterTitle(chapter.getTitle());
            chapterDto.setChapterDescription(chapter.getDescription());
            chapterDto.setChapterNumber(chapter.getChapterNumber());
            chapterDto.setRecordVideo(chapter.getVideoUrl());
            chapterDtos.add(chapterDto);
        }
        dto.setRecordedLectureChapters(chapterDtos);

        return dto;
    }

    private List<LectureDto> generatePresignedUrlsLike(List<LectureDto> lectures) {
        for (LectureDto lecture : lectures) {
            lecture.setRecordThumbnail(s3Service.generatePresignedUrl(lecture.getRecordThumbnail(),
                URL_EXPIRATION_SECONDS)); // 1 hour expiration
        }
        return lectures;
    }

    private List<LectureDto> applyPresignedUrls(List<LectureDto> lectures) {
        Map<String, String> presignedUrls = generatePresignedUrls(lectures);

        for (LectureDto lecture : lectures) {
            lecture.setRecordThumbnail(
                getPresignedUrl(lecture.getRecordThumbnail(), presignedUrls));
            lecture.setRecordThumbnailSmall(
                getPresignedUrl(lecture.getRecordThumbnailSmall(), presignedUrls));
            if (lecture.getRecordedLectureChapters() != null) {
                for (ChapterDto chapter : lecture.getRecordedLectureChapters()) {
                    chapter.setRecordVideo(
                        getPresignedUrl(chapter.getRecordVideo(), presignedUrls));
                }
            }
        }
        return lectures;
    }

    private Map<String, String> generatePresignedUrls(List<LectureDto> lectures) {
        Set<String> keysToGenerate = new HashSet<>();

        for (LectureDto lecture : lectures) {
            addKeyIfNeeded(keysToGenerate, lecture.getRecordThumbnail());
            addKeyIfNeeded(keysToGenerate, lecture.getRecordThumbnailSmall());
            if (lecture.getRecordedLectureChapters() != null) {
                for (ChapterDto chapter : lecture.getRecordedLectureChapters()) {
                    addKeyIfNeeded(keysToGenerate, chapter.getRecordVideo());
                }
            }
        }

        if (keysToGenerate.isEmpty()) {
            return Collections.emptyMap();
        }

        return s3Service.generatePresignedUrls(keysToGenerate, URL_EXPIRATION_SECONDS);
    }


    private void addKeyIfNeeded(Set<String> keysToGenerate, String url) {
        if (url != null && url.startsWith(S3_BASE_URL)) {
            String key = url.substring(S3_BASE_URL.length());
            keysToGenerate.add(key);
        }
    }

    private String getPresignedUrl(String url, Map<String, String> presignedUrls) {
        if (url != null && url.startsWith(S3_BASE_URL)) {
            String key = url.substring(S3_BASE_URL.length());
            return presignedUrls.getOrDefault(key, url);
        }
        return url;
    }

    private String extractS3Key(String url) {
        if (url.startsWith("https://")) {
            String[] parts = url.split("/", 4);
            return parts.length > 3 ? parts[3] : "";
        }
        return url;
    }
}
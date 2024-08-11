package com.yoga.backend.article;

import com.yoga.backend.common.entity.Article;
import java.util.List;
import java.util.Optional;

public interface ArticleService {

    /**
     * 게시글을 저장.
     *
     * @param article 저장할 게시글
     */
    void saveArticle(Article article);

    /**
     * 특정 사용자가 작성한 게시글 조회
     *
     * @param userId 사용자 ID
     * @return 게시글 목록
     */
    List<Article> getArticlesByUserId(int userId);

    /**
     * 게시글 ID로 특정 게시글 조회
     *
     * @param id 게시글 ID
     * @return 게시글
     */
    Optional<Article> getArticleById(Long id);

    /**
     * 게시글 업데이트
     *
     * @param articleId  게시글 ID
     * @param newContent 새로운 게시글 내용
     * @param newImage   새로운 이미지 URL
     * @return 업데이트된 게시글
     */
    Article updateArticle(Long articleId, String newContent, String newImage, String newImageSmall);

    /**
     * 게시글 삭제
     *
     * @param id 삭제할 게시글 ID
     */
    void deleteArticle(Long id);

    /**
     * 내용으로 게시글 조회
     *
     * @param content 게시글 내용
     * @return 게시글 목록
     */
    List<Article> findByContent(String content);
}

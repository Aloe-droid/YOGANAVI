package com.ssafy.yoganavi.data.source.info

import com.ssafy.yoganavi.data.source.YogaDetailResponse
import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.live.LiveLectureData
import com.ssafy.yoganavi.data.source.notice.NoticeData
import com.ssafy.yoganavi.data.source.notice.RegisterNoticeRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoDataSourceImpl @Inject constructor(private val infoAPI: InfoAPI) : InfoDataSource {

    override suspend fun getLectureList(): Response<YogaResponse<LectureData>> =
        infoAPI.getLectureList()

    override suspend fun createLecture(lecture: LectureDetailData): Response<YogaDetailResponse<Boolean>> =
        infoAPI.createLecture(lecture)

    override suspend fun getLecture(recordedId: Long): Response<YogaDetailResponse<LectureDetailData>> =
        infoAPI.getLecture(recordedId)

    override suspend fun updateLecture(lecture: LectureDetailData): Response<YogaDetailResponse<Boolean>> =
        infoAPI.updateLecture(id = lecture.recordedId, lecture = lecture)

    override suspend fun deleteLectures(recordIdList: List<Long>): Response<YogaDetailResponse<Boolean>> =
        infoAPI.deleteLectures(recordIdList)

    override suspend fun likeLecture(
        recordedId: Long,
        like: Boolean
    ): Response<YogaDetailResponse<Boolean>> = infoAPI.likeLecture(recordedId, like)

    override suspend fun getLiveList(): Response<YogaResponse<LiveLectureData>> =
        infoAPI.getLiveList()

    override suspend fun getLive(liveId: Int): Response<YogaDetailResponse<LiveLectureData>> =
        infoAPI.getLive(liveId)

    override suspend fun createLive(): Response<YogaDetailResponse<Unit>> =
        infoAPI.createLive()

    override suspend fun updateLive(liveId: Int): Response<YogaDetailResponse<Unit>> =
        infoAPI.updateLive(liveId)

    override suspend fun deleteLive(liveId: Int): Response<YogaDetailResponse<Unit>> =
        infoAPI.deleteLive(liveId)

    override suspend fun getNoticeList(): Response<YogaResponse<NoticeData>> =
        infoAPI.getNoticeList()

    override suspend fun getNotice(articleId: Int): Response<YogaDetailResponse<NoticeData>> =
        infoAPI.getNotice(articleId)

    override suspend fun insertNotice(registerNoticeRequest: RegisterNoticeRequest): Response<YogaDetailResponse<Unit>> =
        infoAPI.insertNotice(registerNoticeRequest)

    override suspend fun updateNotice(
        registerNoticeRequest: RegisterNoticeRequest,
        articleId: Int
    ): Response<YogaDetailResponse<Unit>> = infoAPI.updateNotice(registerNoticeRequest, articleId)

    override suspend fun deleteNotice(articleId: Int): Response<YogaDetailResponse<Unit>> =
        infoAPI.deleteNotice(articleId)
}
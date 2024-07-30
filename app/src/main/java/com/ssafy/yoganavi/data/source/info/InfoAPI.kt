package com.ssafy.yoganavi.data.source.info

import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.data.source.dto.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.dto.live.LiveLectureData
import com.ssafy.yoganavi.data.source.dto.mypage.Profile
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.data.source.dto.notice.RegisterNoticeRequest
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import com.ssafy.yoganavi.data.source.response.YogaDetailResponse
import com.ssafy.yoganavi.data.source.response.YogaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InfoAPI {

    @GET("mypage/info")
    suspend fun getProfile(): Response<YogaDetailResponse<Profile>>

    @POST("mypage/update")
    suspend fun updateProfile(@Body profile: Profile): Response<YogaDetailResponse<Profile>>

    //TEACHER
    @GET("teacher")
//    suspend fun getTeacherList(@Body filter:FilterData): Response<YogaResponse<TeacherData>>
    suspend fun getTeacherList(): Response<YogaResponse<TeacherData>>

    // LECTURE
    @GET("mypage/recorded-lecture/list")
    suspend fun getLectureList(): Response<YogaResponse<LectureData>>

    @POST("mypage/recorded-lecture/create")
    suspend fun createLecture(@Body lecture: LectureDetailData): Response<YogaDetailResponse<Boolean>>

    @GET("mypage/recorded-lecture/detail/{recorded_id}")
    suspend fun getLecture(@Path("recorded_id") id: Long): Response<YogaDetailResponse<LectureDetailData>>

    @PUT("mypage/recorded-lecture/update/{recorded_id}")
    suspend fun updateLecture(
        @Path("recorded_id") id: Long,
        @Body lecture: LectureDetailData
    ): Response<YogaDetailResponse<Boolean>>

    @POST("mypage/recorded-lecture/delete")
    suspend fun deleteLectures(@Body body: HashMap<String, List<Long>>): Response<YogaDetailResponse<Boolean>>

    @POST("recorded-lecture/like/{recorded_id}")
    suspend fun likeLecture(@Path("recorded_id") id: Long): Response<YogaDetailResponse<Boolean>>

    // Live
    @GET("mypage/live-lecture-manage")
    suspend fun getLiveList(): Response<YogaResponse<LiveLectureData>>

    @GET("mypage/live-lecture-manage/{live_id}")
    suspend fun getLive(@Path("live_id") id: Int): Response<YogaDetailResponse<LiveLectureData>>

    @POST("mypage/live-lecture-manage/create")
    suspend fun createLive(@Body liveLectureData: LiveLectureData): Response<YogaDetailResponse<Unit>>

    @PUT("mypage/live-lecture-manage/update/{live_id}")
    suspend fun updateLive(
        @Body liveLectureData: LiveLectureData,
        @Path("live_id") liveId: Int
    ): Response<YogaDetailResponse<Unit>>

    @DELETE("mypage/live-lecture-manage/delete/{live_id}")
    suspend fun deleteLive(@Path("live_id") liveId: Int): Response<YogaDetailResponse<Unit>>

    @GET("mypage/notification/list")
    suspend fun getNoticeList(): Response<YogaResponse<NoticeData>>

    @GET("mypage/notification/update/{article_id}")
    suspend fun getNotice(@Path("article_id") id: Int): Response<YogaDetailResponse<NoticeData>>

    @POST("mypage/notification/write")
    suspend fun insertNotice(@Body registerNoticeRequest: RegisterNoticeRequest): Response<YogaDetailResponse<Unit>>

    @PUT("mypage/notification/update/{article_id}")
    suspend fun updateNotice(
        @Body registerNoticeRequest: RegisterNoticeRequest,
        @Path("article_id") id: Int
    ): Response<YogaDetailResponse<Unit>>

    @DELETE("mypage/notification/delete/{article_id}")
    suspend fun deleteNotice(@Path("article_id") id: Int): Response<YogaDetailResponse<Unit>>
}

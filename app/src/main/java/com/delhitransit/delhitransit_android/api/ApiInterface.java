package com.delhitransit.delhitransit_android.api;

import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    @POST("delhitransit-admin/v1/init/stops")
    Call<Void> initStops();

    @GET("delhitransit/v1/stops")
    Call<List<StopsResponseData>> getStops();

    /*@POST("likedQueriesAndResponseList")
    Call<LikedQueriesAndResponseListResponse> requestQueriesAndResponseList(@Header("Authorization") String token, @Body LikedQueriesAndResponseListRequestData request);

    @POST("articles")
    Call<ArticlesListResponse> requestArticles(@Header("Authorization") String token, @Body ArticlesAndVideoRequestData request, @Query("pageNum") int page);

    @POST("videos")
    Call<VideoListResponse> requestVideos(@Header("Authorization") String token, @Body ArticlesAndVideoRequestData request, @Query("pageNum") int page);

    @POST("cancelOnlineConsultRequests")
    Call<OnlyMessageResponse> cancelOnlineConsult(@Header("Authorization") String token, @Body CancelOnlineConsultRequestBody request);


    @Headers({"Authorization: key=" + SlotsActivity.SERVER_KEY, "Content-Type:application/json"})
    @POST("https://fcm.googleapis.com/fcm/send")
    Call<NewAppointmentResponse> sendFCMtoDoctor(@Body NewAppointmentRequestData request);

    @POST("jobApplicants")
    Call<JobRequestData.JobRequestDataResponse> sendApplicationRequest(@Header("Authorization") String token, @Body JobRequestData request);

    @GET("https://api.covid19india.org/data.json")
    Call<CovidStatsDataResponse> getCovidStatsData();


    @POST("addReview")
    Call<SubmitReviewData.OnlyMessageResponseForSubitReview> submitReview(@Header("Authorization") String token, @Body SubmitReviewData request);


    @POST("viewReview")
    Call<DoctorReviewResponseDataWithPage> requestDoctorReviewsPageWise(@Header("Authorization") String token, @Body DoctorReviewData reviewData, @Query("pageNum") int page);
*/
}

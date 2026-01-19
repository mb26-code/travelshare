package dev.mb_labs.travelshare.api;

import java.util.List;

import dev.mb_labs.travelshare.model.AuthResponse;
import dev.mb_labs.travelshare.model.Comment;
import dev.mb_labs.travelshare.model.CommentRequest;
import dev.mb_labs.travelshare.model.ConfirmResetRequest;
import dev.mb_labs.travelshare.model.Frame;
import dev.mb_labs.travelshare.model.LoginRequest;
import dev.mb_labs.travelshare.model.PasswordResetRequest;
import dev.mb_labs.travelshare.model.RegisterRequest;
import dev.mb_labs.travelshare.model.User;
import dev.mb_labs.travelshare.model.VerifyRequest;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TravelShareService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<User> register(@Body RegisterRequest request);

    @POST("auth/register/confirm")
    Call<ResponseBody> verifyEmail(@Body VerifyRequest request);

    @POST("auth/password-reset")
    Call<ResponseBody> requestPasswordReset(@Body PasswordResetRequest request);

    @POST("auth/password-reset/confirm")
    Call<ResponseBody> confirmPasswordReset(@Body ConfirmResetRequest request);

    @GET("frames")
    Call<List<Frame>> getFrames(@Header("Authorization") String token);

    @GET("frames")
    Call<List<Frame>> searchFrames(@Query("q") String query);

    @Multipart
    @POST("frames")
    Call<Frame> createFrame(
            @Header("Authorization") String token,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("visibility") RequestBody visibility,
            @Part("photoMetadata") RequestBody photoMetadata,
            @Part List<MultipartBody.Part> photos
    );

    //Likes
    @POST("frames/{id}/likes")
    Call<ResponseBody> likeFrame(@Header("Authorization") String token, @Path("id") int frameId);

    @DELETE("frames/{id}/likes")
    Call<ResponseBody> unlikeFrame(@Header("Authorization") String token, @Path("id") int frameId);

    //Comments
    @GET("frames/{id}/comments")
    Call<List<Comment>> getFrameComments(@Path("id") int frameId);

    @POST("frames/{id}/comments")
    Call<Comment> postComment(@Header("Authorization") String token, @Path("id") int frameId, @Body CommentRequest request);
}
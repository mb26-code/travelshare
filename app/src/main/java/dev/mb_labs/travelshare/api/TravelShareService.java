package dev.mb_labs.travelshare.api;

import java.util.List;

import dev.mb_labs.travelshare.model.AuthResponse;
import dev.mb_labs.travelshare.model.Frame;
import dev.mb_labs.travelshare.model.LoginRequest;
import dev.mb_labs.travelshare.model.RegisterRequest;
import dev.mb_labs.travelshare.model.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface TravelShareService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<User> register(@Body RegisterRequest request);

    @GET("frames")
    Call<List<Frame>> getFrames();

    @GET("frames")
    Call<List<Frame>> searchFrames(@Query("q") String query);

    @Multipart
    @POST("frames")
    Call<Frame> createFrame(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("visibility") RequestBody visibility,
            @Part("photoMetadata") RequestBody photoMetadata,
            @Part List<MultipartBody.Part> photos
    );
}


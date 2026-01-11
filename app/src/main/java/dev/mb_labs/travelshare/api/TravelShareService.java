package dev.mb_labs.travelshare.api;

import java.util.List;

import dev.mb_labs.travelshare.model.AuthResponse;
import dev.mb_labs.travelshare.model.Frame;
import dev.mb_labs.travelshare.model.LoginRequest;
import dev.mb_labs.travelshare.model.RegisterRequest;
import dev.mb_labs.travelshare.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface TravelShareService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<User> register(@Body RegisterRequest request);

    @GET("frames")
    Call<List<Frame>> getFrames();

    //...
}


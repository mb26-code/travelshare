package dev.mb_labs.travelshare.api;

import dev.mb_labs.travelshare.model.AuthResponse;
import dev.mb_labs.travelshare.model.LoginRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TravelShareService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    //...
}


package dev.mb_labs.travelshare.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static final String BASE_URL = "https://api.travelshare.mb-labs.dev/";

    private static Retrofit retrofit = null;

    public static TravelShareService getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(TravelShareService.class);
    }
}


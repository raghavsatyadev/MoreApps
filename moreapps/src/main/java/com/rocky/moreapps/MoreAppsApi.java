package com.rocky.moreapps;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MoreAppsApi {
    @GET
    Call<List<MoreAppsModel>> getAppModel(@Url String url);
}
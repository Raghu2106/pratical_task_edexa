package com.raghu.viitorclouddemo.retrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RequestApi {

    @GET("/81ada0361bbd877efb9e")
    Call<ArrayList<EmployeeModel>> getEmployee();
}

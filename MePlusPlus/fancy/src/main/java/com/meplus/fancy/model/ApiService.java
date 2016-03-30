package com.meplus.fancy.model;

import com.meplus.fancy.model.entity.Address;
import com.meplus.fancy.model.entity.Book;
import com.meplus.fancy.model.entity.Response;
import com.meplus.fancy.model.entity.User;

import java.util.List;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Field;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by dandanba on 3/9/16.
 */
public interface ApiService {

    @GET("geocoding")
    Observable<Address> geocoding(@Query("a") String address);

    @FormUrlEncoded
    @POST("api/borrow/getborrowlistbyrobot")
    Observable<Response<User>> getborrowlistbyrobot(@Field("Data") String data, @Field("LibraryId") String libraryId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign);

    @FormUrlEncoded
    @POST("api/borrow/borrowbyrobot")
    Observable<Response<Book>> borrowbyrobot(@Field("Data") String data, @Field("LibraryId") String libraryId, @Field("UserId") String userId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign);

    @FormUrlEncoded
    @POST("api/borrow/returnbyrobot")
    Observable<Response<Book>> returnbyrobot(@Field("Data") String data, @Field("LibraryId") String libraryId, @Field("UserId") String userId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign);

    @FormUrlEncoded
    @POST("api/borrow/getborrowedlistbyrobot")
    Observable<Response<List<Book>>> getborrowedlistbyrobot(@Field("Data") String data, @Field("LibraryId") String libraryId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign);
}

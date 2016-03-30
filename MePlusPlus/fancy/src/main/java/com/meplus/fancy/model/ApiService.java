package com.meplus.fancy.model;

import com.meplus.fancy.model.entity.Address;
import com.meplus.fancy.model.entity.Book;
import com.meplus.fancy.model.entity.Response;
import com.meplus.fancy.model.entity.User;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by dandanba on 3/9/16.
 */
public interface ApiService {

    @GET("geocoding")
    Observable<Address> geocoding(@Query("a") String address);

    @POST("api/borrow/getborrowlistbyrobot")
    Observable<Response<User>> getborrowlistbyrobot(@Query("Data") String data, @Query("LibraryId") String libraryId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign);

    @POST("api/borrow/borrowbyrobot")
    Observable<Response<Book>> borrowbyrobot(@Query("Data") String data, @Query("LibraryId") String libraryId, @Query("UserId") String userId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign);

    @POST("api/borrow/returnbyrobot")
    Observable<Response<Book>> returnbyrobot(@Query("Data") String data, @Query("LibraryId") String libraryId, @Query("UserId") String userId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign);

    @POST("api/borrow/getborrowedlistbyrobot")
    Observable<Response<List<Book>>> getborrowedlistbyrobot(@Query("Data") String data, @Query("LibraryId") String libraryId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign);
}

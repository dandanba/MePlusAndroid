package com.meplus.fancy.model;

import com.meplus.fancy.model.entity.Address;
import com.meplus.fancy.model.entity.Book;
import com.meplus.fancy.model.entity.Response;
import com.meplus.fancy.model.entity.User;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by dandanba on 3/9/16.
 */
public class FancyApi implements ApiService {


    @Override
    public Observable<Address> geocoding(@Query("a") String address) {
        return null;
    }

    @Override
    public Observable<Response<User>> getborrowlistbyrobot(@Field("Data") String data, @Field("LibraryId") String libraryId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign) {
        return null;
    }

    @Override
    public Observable<Response<Book>> borrowbyrobot(@Field("Data") String data, @Field("LibraryId") String libraryId, @Field("UserId") String userId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign) {
        return null;
    }

    @Override
    public Observable<Response<Book>> returnbyrobot(@Field("Data") String data, @Field("LibraryId") String libraryId, @Field("UserId") String userId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign) {
        return null;
    }

    @Override
    public Observable<Response<List<Book>>> getborrowedlistbyrobot(@Field("Data") String data, @Field("LibraryId") String libraryId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign) {
        return null;
    }
}

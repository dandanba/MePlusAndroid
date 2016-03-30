package com.meplus.fancy.model;

import com.meplus.fancy.model.entity.Address;
import com.meplus.fancy.model.entity.Book;
import com.meplus.fancy.model.entity.Response;
import com.meplus.fancy.model.entity.User;

import java.util.List;

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
    public Observable<Response<User>> getborrowlistbyrobot(@Query("Data") String data, @Query("LibraryId") String libraryId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign) {
        return null;
    }

    @Override
    public Observable<Response<Book>> borrowbyrobot(@Query("Data") String data, @Query("LibraryId") String libraryId, @Query("UserId") String userId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign) {
        return null;
    }

    @Override
    public Observable<Response<Book>> returnbyrobot(@Query("Data") String data, @Query("LibraryId") String libraryId, @Query("UserId") String userId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign) {
        return null;
    }

    @Override
    public Observable<Response<List<Book>>> getborrowedlistbyrobot(@Query("Data") String data, @Query("LibraryId") String libraryId, @Header("X-FANCY-TIMESTAMP") long timestamp, @Header("X-FANCY-SIGN") String sign) {
        return null;
    }
}

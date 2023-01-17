package com.example.badiefarzandiassignment2.data.db;

public interface DbResponse<T> {
    void onSuccess(T t);

    void onError(Error error);
}

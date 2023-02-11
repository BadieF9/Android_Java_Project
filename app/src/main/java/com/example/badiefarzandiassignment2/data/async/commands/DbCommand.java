package com.example.badiefarzandiassignment2.data.async.commands;

import com.example.badiefarzandiassignment2.data.async.DbResult;

public interface DbCommand<T> {

    DbResult<T> execute();
}

package com.lab2.service;

import java.util.Optional;
import java.util.List;

public interface Service<T, D, ID> {
    Optional<T> findRecord(ID value);
    List<T> getAllRecords();
    void addRecord(D dto);
    boolean updateRecord(D dto);
    boolean deleteRecord(ID value);
    T convertToEntity(D dto);
}
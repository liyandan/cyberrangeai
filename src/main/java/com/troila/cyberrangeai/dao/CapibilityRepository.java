package com.troila.cyberrangeai.dao;

import com.troila.cyberrangeai.pojo.Capibility;
import org.springframework.data.repository.CrudRepository;

public interface CapibilityRepository extends CrudRepository<Capibility, Integer> {

    Iterable<Capibility> findAllById(String id);

    void deleteAllById(String id);
}
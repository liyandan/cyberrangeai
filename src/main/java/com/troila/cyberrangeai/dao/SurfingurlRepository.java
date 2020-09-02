package com.troila.cyberrangeai.dao;

import com.troila.cyberrangeai.pojo.SurfingUrls;
import org.springframework.data.repository.CrudRepository;

public interface SurfingurlRepository extends CrudRepository<SurfingUrls, Integer> {

    Iterable<SurfingUrls> findAllById(String id);

    void deleteAllById(String id);
}
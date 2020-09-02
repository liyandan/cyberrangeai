package com.troila.cyberrangeai.dao;

import com.troila.cyberrangeai.pojo.EmailTo;
import org.springframework.data.repository.CrudRepository;

public interface EmailtoRepository extends CrudRepository<EmailTo, Integer> {
    Iterable<EmailTo> findAllById(String id);

    void deleteAllById(String id);
}
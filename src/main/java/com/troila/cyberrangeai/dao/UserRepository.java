package com.troila.cyberrangeai.dao;

import com.troila.cyberrangeai.pojo.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

    void deleteAllByUserid(String userId);

    void deleteByUseridAndScenarioid(String uid,String sid);

}
package com.troila.cyberrangeai.service;

import com.troila.cyberrangeai.dao.UserRepository;
import com.troila.cyberrangeai.pojo.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional
@Service("UserService")
public class UserService {

    @Resource
    private UserRepository userRepository;


    /**
     *
     * update, save, delete操作
     */

    //保存数据
    public void save(User user)
    {
        userRepository.save(user);
    }

    //删除数据
    public void delete(String uid,String sid)
    {
        userRepository.deleteByUseridAndScenarioid(uid,sid);
    }

    //查询
    public Iterable<User> getAll() {
        return userRepository.findAll();
    }


    //更新UserID和ScenarioID
    public void update(String userId,String scenarioId,String userEmail){

        User user = new User();
        user.setScenarioid(scenarioId);
        user.setUserid(userId);
        user.setMail(userEmail);
        userRepository.save(user);
    }





}

package com.troila.cyberrangeai.service;

import com.troila.cyberrangeai.dao.CapibilityRepository;
import com.troila.cyberrangeai.pojo.Capibility;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional
@Service("CapibilityService")
public class CapibilityService {

    @Resource
    private CapibilityRepository capibilityRepository;

    /**
     *
     * update, save, delete操作
     */

    //保存数据
    public void save(Capibility capibility)
    {
        capibilityRepository.save(capibility);
    }

    //删除数据
    public void delete(Capibility capibility)
    {
        capibilityRepository.delete(capibility);
    }

    // 删除所有跟id关联的数据
    public void deleteAllById(String id){
        capibilityRepository.deleteAllById(id);
    }

    //查询所有数据
    public Iterable<Capibility> getAll() {
        return capibilityRepository.findAll();
    }

    public Iterable<Capibility> getAllById(String id){
        return capibilityRepository.findAllById(id);
    }

    //更新浏览网页的表
    public void update(String id ,String action,float weight){
        Capibility capibility = new Capibility();
        capibility.setId(id);
        capibility.setAction(action);
        capibility.setWeight(String.valueOf(weight));
        save(capibility);
    }

}

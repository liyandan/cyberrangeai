package com.troila.cyberrangeai.service;

import com.troila.cyberrangeai.dao.SurfingurlRepository;
import com.troila.cyberrangeai.pojo.SurfingUrls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional
@Service("SurfingurlService")
public class SurfingurlService {

    @Resource
    private SurfingurlRepository surfingurlRepository;

    /**
     *
     * update, save, delete操作
     */

    //保存数据
    public void save(SurfingUrls surfingUrls)
    {
        surfingurlRepository.save(surfingUrls);
    }

    //删除数据
    public void delete(SurfingUrls surfingUrls)
    {
        surfingurlRepository.delete(surfingUrls);
    }

    // 删除所有跟id关联的数据
    public void deleteAllById(String id){
        surfingurlRepository.deleteAllById(id);
    }

    //查询所有数据
    public Iterable<SurfingUrls> getAll() {
        return surfingurlRepository.findAll();
    }

    public Iterable<SurfingUrls> getAllById(String id){
        return surfingurlRepository.findAllById(id);
    }

    //更新浏览网页的表
    public void update(String id ,String url){
        SurfingUrls surfurl = new SurfingUrls();
        surfurl.setId(id);
        surfurl.setUrl(url);
        save(surfurl);
    }

}

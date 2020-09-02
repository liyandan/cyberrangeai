package com.troila.cyberrangeai.service;

import com.troila.cyberrangeai.dao.EmailtoRepository;
import com.troila.cyberrangeai.pojo.EmailTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional
@Service("EmailtoService")
public class EmailtoService {

    @Resource
    private EmailtoRepository emailtoRepository;


    /**
     *
     * update, save, delete操作
     */

    //保存数据
    public void save(EmailTo emailTo)
    {
        emailtoRepository.save(emailTo);
    }

    //删除数据
    public void delete(EmailTo emailTo)
    {
        emailtoRepository.delete(emailTo);
    }

    //删除所有跟id关联的数据
    public void deleteAllById(String id){
        emailtoRepository.deleteAllById(id);
    }

    //查询所有
    public Iterable<EmailTo> getAll() {
        return emailtoRepository.findAll();
    }

    //按照ID查询
    public Iterable<EmailTo> getAllById(String id){
        return emailtoRepository.findAllById(id);
    }

    //更新
    public void update(String id, String fullname,String email){
        EmailTo   emailTo = new EmailTo();
        emailTo.setId(id);
        emailTo.setFullname(fullname);
        emailTo.setEmail(email);
        save(emailTo);
    }
}

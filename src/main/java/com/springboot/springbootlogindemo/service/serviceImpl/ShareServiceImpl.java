package com.springboot.springbootlogindemo.service.serviceImpl;

import com.springboot.springbootlogindemo.domain.Share;
import com.springboot.springbootlogindemo.repository.ShareDao;
import com.springboot.springbootlogindemo.service.ShareService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class ShareServiceImpl implements ShareService {
    @Resource
    private ShareDao shareDao;

    @Override
    public List<Share> findAll() {
        return shareDao.findAll();
    }

    @Override
    public void saveShare(Share share) {
        shareDao.save(share);
    }
    @Transactional
    public void deleteShare(String id) {
        shareDao.deleteShare(id);
    }
    @Override
    public Share findById(String id){
        return shareDao.findById(id);
    }

}

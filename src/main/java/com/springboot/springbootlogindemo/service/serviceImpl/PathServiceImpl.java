package com.springboot.springbootlogindemo.service.serviceImpl;


import com.springboot.springbootlogindemo.domain.SavePath;
import com.springboot.springbootlogindemo.domain.Share;
import com.springboot.springbootlogindemo.repository.PathDao;
import com.springboot.springbootlogindemo.service.PathService;
import com.springboot.springbootlogindemo.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PathServiceImpl implements PathService {
    @Resource
    private PathDao pathDao;

    @Override
    public List<SavePath> findAll() {
        List<SavePath> path=pathDao.findAll();
        return path;
    }
}

package com.springboot.springbootlogindemo.service;

import com.springboot.springbootlogindemo.domain.Share;

import java.util.List;

public interface ShareService {
    List<Share> findAll();
    void saveShare(Share share);

    void deleteById(String id);

    Share findById(String id);
    // 不需要再定义 updateShareById 方法，使用 save 方法实现更新逻辑
}

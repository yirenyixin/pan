package com.springboot.springbootlogindemo.service;



import com.springboot.springbootlogindemo.domain.SavePath;

import java.util.List;

public interface PathService {
    List<SavePath> findAll();
}

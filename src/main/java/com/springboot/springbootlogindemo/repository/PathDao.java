package com.springboot.springbootlogindemo.repository;


import com.springboot.springbootlogindemo.domain.SavePath;
import com.springboot.springbootlogindemo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PathDao extends JpaRepository<SavePath, Long> {
    List<SavePath> findAll();
}

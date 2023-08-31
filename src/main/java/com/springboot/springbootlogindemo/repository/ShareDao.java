package com.springboot.springbootlogindemo.repository;

import com.springboot.springbootlogindemo.domain.SavePath;
import com.springboot.springbootlogindemo.domain.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareDao extends JpaRepository<Share, Long> {
//    List<Share> findAll();
void deleteById(String id);//还不能删除
    Share findById(String id);
}

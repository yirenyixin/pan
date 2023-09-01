package com.springboot.springbootlogindemo.repository;

import com.springboot.springbootlogindemo.domain.SavePath;
import com.springboot.springbootlogindemo.domain.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareDao extends JpaRepository<Share, Long> {
//    List<Share> findAll();
    @Modifying
    @Query("DELETE FROM Share s WHERE s.id = :id")
    void deleteShare(@Param("id") String id);//还不能删除
    Share findById(String id);
}

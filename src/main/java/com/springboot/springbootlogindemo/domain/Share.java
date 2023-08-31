package com.springboot.springbootlogindemo.domain;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
@Table(name = "share")
@Entity
public class Share {
    @Column(name = "save_path")
    private String save_path;//存储路径
    private String pwd;
    @Id
    private String id;
    private String detail_path;
    private String creat_time;

    public String getSave_path() {
        return save_path;
    }

    public void setSave_path(String save_path) {
        this.save_path = save_path;
    }

    public String getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(String creat_time) {
        this.creat_time = creat_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    private String end_time;

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetail_path() {
        return detail_path;
    }

    @Override
    public String toString() {
        return "Share{" +
                "save_path='" + save_path + '\'' +
                ", pwd='" + pwd + '\'' +
                ", id='" + id + '\'' +
                ", detail_path='" + detail_path + '\'' +
                ", creat_time='" + creat_time + '\'' +
                ", end_time='" + end_time + '\'' +
                '}';
    }

    public void setDetail_path(String detail_path) {
        this.detail_path = detail_path;
    }
// Constructors, getters, and setters
}


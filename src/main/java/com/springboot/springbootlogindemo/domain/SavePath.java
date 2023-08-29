package com.springboot.springbootlogindemo.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "path")
@Entity
public class SavePath {
    @Id
    private String save_path;//存储路径
    private long   space;//空间



    public String getSave_path() {
        return save_path;
    }

    public void setSave_path(String save_path) {
        this.save_path = save_path;
    }

    public long getSpace() {
        return space;
    }

    public void setSpace(long space) {
        this.space = space;
    }
}

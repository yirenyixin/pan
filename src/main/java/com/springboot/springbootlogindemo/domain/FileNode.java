package com.springboot.springbootlogindemo.domain;

import java.util.List;

public class FileNode {
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

    public List<FileNode> getChildren() {
        return children;
    }

    public void setChildren(List<FileNode> children) {
        this.children = children;
    }

    private List<FileNode> children;

    // Constructors, getters, and setters
}


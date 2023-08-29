package com.springboot.springbootlogindemo.domain;

import javax.persistence.*;
import java.math.BigInteger;

@Table(name = "user")
@Entity
public class User {
    // 注意属性名要与数据表中的字段名一致
    // 主键自增int(10)对应long
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long uid;

    // 用户名属性varchar对应String
    private String uname;

    // 密码属性varchar对应String
    private String password;
    private long user_space;
    private long total_space;

    public long getUse_space() {
        return user_space;
    }

    public void setUse_space(long use_space) {
        this.user_space = use_space;
    }

    public long getTotal_space() {
        return total_space;
    }

    public void setTotal_space(long total_space) {
        this.total_space = total_space;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

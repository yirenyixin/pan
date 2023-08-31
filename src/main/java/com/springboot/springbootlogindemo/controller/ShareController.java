package com.springboot.springbootlogindemo.controller;

import java.util.UUID;

public class ShareController {

    // 生成唯一的分享链接
    String generateUniqueLink() {
        return "http://yourdomain.com/share/" + UUID.randomUUID().toString();
    }

    // 生成随机密码
    String generateRandomPassword() {
        // 生成随机密码的逻辑
        // 例如，生成一个由数字和字母组成的6位密码
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }

    // ...其他代码
}
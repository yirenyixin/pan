package com.springboot.springbootlogindemo.controller;

import com.springboot.springbootlogindemo.domain.Share;
import com.springboot.springbootlogindemo.service.PathService;
import com.springboot.springbootlogindemo.service.ShareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/share")
public class ShareController {
    @Resource
    private ShareService shareService;
    @Resource
    private PathService pathService;
    @PostMapping("/check")
    public ResponseEntity<Map<String, String>> check(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        String pwd = request.get("pwd");
        System.out.println(id);
        System.out.println(pwd);
        Share share=shareService.findById(id);
        System.out.println(share.toString());
        Map<String, String> response = new HashMap<>();
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = LocalDate.parse(share.getEnd_time(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(pwd.equals(share.getPwd())&&!currentDate.isAfter(endDate)){
            response.put("path",share.getSave_path()+share.getDetail_path());
        }else {
            response.put("path",null);
        }
        return ResponseEntity.ok(response);
    }
    // 生成唯一的分享链接
    @CrossOrigin(origins = "http://localhost:8080/", maxAge = 3600)
    @PostMapping("/shareUrl")
    public ResponseEntity<Map<String, String>> shareFiles(@RequestBody List<String> pathsToShare) {
        Share share=new Share();
        String getPath=pathService.findAll().get(0).getSave_path()+"/";
        share.setSave_path(getPath);
        share.setDetail_path(pathsToShare.get(0));
        share.setId(UUID.randomUUID().toString());
        // 设置开始时间为当前日期
        LocalDate currentDate = LocalDate.now();
        share.setCreat_time(currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        // 设置结束时间为开始时间加7天
        LocalDate endTime = currentDate.plusDays(7);
        share.setEnd_time(endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        // 生成分享链接和密码的逻辑
        String shareLink = generateUniqueLink(share.getId());
        String sharePassword = generateRandomPassword();
        share.setPwd(sharePassword);
        // 存储链接和密码的逻辑，可以使用数据库等方式
        Map<String, String> response = new HashMap<>();
        response.put("shareLink", shareLink);
        response.put("sharePassword", sharePassword);
        shareService.saveShare(share);
        return ResponseEntity.ok(response);
    }
    String generateUniqueLink(String uuid) {
//        return "http://localhost:8080/share/" + UUID.randomUUID().toString();
        try {
            String encodedPath = URLEncoder.encode(uuid, "UTF-8");
            return "http://localhost:8080/share/" + encodedPath;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
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
    //保存文件
    @PostMapping("/savePath")
    public ResponseEntity<Map<String, Object>> savePath(@RequestBody Map<String, String> requestData) {
        String currentPath = requestData.get("currentPath");
        String currentRoute = requestData.get("currentRoute");
        System.out.println(currentPath);
        System.out.println(currentRoute);
        // 在这里执行你的保存逻辑，比如将路径信息存储到数据库或者进行其他操作
        // ...

        Map<String, Object> response = new HashMap<>();
        response.put("success", true); // 假设保存成功
        return ResponseEntity.ok(response);
    }
    // ...其他代码
}
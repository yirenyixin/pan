package com.springboot.springbootlogindemo.controller;

import com.springboot.springbootlogindemo.domain.Share;
import com.springboot.springbootlogindemo.service.PathService;
import com.springboot.springbootlogindemo.service.ShareService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.*;
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
        // 生成一个由数字和字母组成的4位密码
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 4; i++) {
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
        Share share=shareService.findById(currentRoute);
        System.out.println(currentPath);
        System.out.println(currentRoute);
        System.out.println(share.toString());
        String path=share.getSave_path()+share.getDetail_path();
        currentPath=pathService.findAll().get(0).getSave_path()+"/"+currentPath;
        path=path.replace("//","/");
        currentPath=currentPath.replace("//","/");//目标文件夹路径
        System.out.println(currentPath);
        String message = "";
        try {
            if (Files.exists(Paths.get(path))&&path.endsWith("/")) { // 检查原始路径是否存在
                Path sourcePath = Paths.get(path);//要复制的文件路径
                Path targetPath = Paths.get(currentPath); // 目标文件夹路径
                copyDirectory(sourcePath, targetPath.resolve(sourcePath.getFileName()));//targetPath.resolve(sourcePath.getFileName())将sourcePath文件名添加到targetPath后
            } else if (Files.exists(Paths.get(path))&&!path.endsWith("/")) {
                Path sourcePath = Paths.get(path);//要复制的文件路径
                Path targetPath = Paths.get(currentPath); // 目标文件夹路径
                Files.copy(sourcePath, targetPath.resolve(sourcePath.getFileName()));//复制文件
            } else {
                message = "路径不存在";
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "复制失败";
        }
        Map<String, Object> response = new HashMap<>();
        if (!message.isEmpty()) {
            response.put("error", false);
        }else {
            response.put("success", true);
        }
            return ResponseEntity.ok(response);
    }
    //复制文件夹里的文件
    private void copyDirectory(Path sourceDir, Path targetDir) throws IOException {
        // 遍历源文件夹内的所有内容，包括子文件夹和文件
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
            for (Path source : stream) {
                // 构建目标路径，将源路径相对于源文件夹的相对路径添加到目标文件夹路径中
                Path target = targetDir.resolve(sourceDir.relativize(source));

                try {
                    if (Files.isDirectory(source)) { // 检查当前路径是否是文件夹
                        // 如果是文件夹，创建目标文件夹路径
                        Files.createDirectories(target);
                        copyDirectory(source, target); // 递归处理子文件夹
                    } else {
                        // 如果是文件，使用 Files.copy() 复制文件到目标路径
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
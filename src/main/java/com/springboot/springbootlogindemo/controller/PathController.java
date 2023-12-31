package com.springboot.springbootlogindemo.controller;

import com.springboot.springbootlogindemo.domain.SavePath;
import com.springboot.springbootlogindemo.domain.User;
import com.springboot.springbootlogindemo.service.PathService;
import com.springboot.springbootlogindemo.service.serviceImpl.utils.Result;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/path")
public class PathController {
    @Resource
    private PathService pathService;

    @PostMapping("/get")
    public ResponseEntity<Map<String, Object>> getController(@RequestBody Map<String, String> requestBody) {

        String uname = requestBody.get("uname"); // 获取前端传递的uname值

        String rootPath = pathService.findAll().get(0).getSave_path(); // 根据你的需求设置根路径
        File rootDirectory = new File(rootPath);
        System.out.println(uname);
        System.out.println(rootPath);
        File[] subDirectories = rootDirectory.listFiles(File::isDirectory);
        Map<String, Object> response = new HashMap<>();

        if (subDirectories != null) {
            for (File subDir : subDirectories) {
                if (subDir.getName().equals(uname)) {
                    Map<String, Object> rootInfo = new HashMap<>();
                    rootInfo.put("save_path", subDir.getName() + "/");
                    rootInfo.put("space", 0);
                    rootInfo.put("children", new ArrayList<>()); // 初始化空的子目录列表
                    rootInfo.put("uname", uname);
                    printDirectoryTree(subDir, rootInfo, 1);
                    response = rootInfo;
                    break; // 只处理第一个匹配的子目录
                }
            }
        }

        System.out.println(response);
        return ResponseEntity.ok(response);
    }

    // 递归打印目录树
    private void printDirectoryTree(File directory, Map<String, Object> parentInfo, int depth) {
        File[] fileList = directory.listFiles();
        if (fileList != null) {
            List<Map<String, Object>> children = new ArrayList<>();
            for (File file : fileList) {
                Map<String, Object> fileInfo = new HashMap<>();
                String savePath = file.isDirectory() ? file.getName() + "/" : file.getName();
                fileInfo.put("save_path", savePath);
                fileInfo.put("space", depth * 2 - 1); // 根据需要自定义缩进空格数量

                if (!file.isDirectory()) {
                    fileInfo.put("file_type", getFileType(file.getName()));
                }

                // 在这里将子节点添加到父节点的children列表中
                children.add(fileInfo);

                if (file.isDirectory()) {
                    fileInfo.put("children", new ArrayList<>()); // 初始化空的子目录列表
                    printDirectoryTree(file, fileInfo, depth + 1);
                }
            }
            parentInfo.put("children", children); // 将children列表添加到父节点
        }
    }

    // 获取文件类型
    private String getFileType(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    @PostMapping("/getfile")
    public ResponseEntity<Map<String, Object>> getFileController(@RequestBody Map<String, String> requestBody) {
        String uname = requestBody.get("uname"); // 获取前端传递的uname值
        String rootPath = pathService.findAll().get(0).getSave_path(); // 根据你的需求设置根路径
        File rootDirectory = new File(rootPath);
        System.out.println(uname);
        System.out.println(rootPath);
        File[] subDirectories = rootDirectory.listFiles(File::isDirectory);
        Map<String, Object> response = new HashMap<>();
        if (subDirectories != null) {
            for (File subDir : subDirectories) {
                if (subDir.getName().equals(uname)) {
                    Map<String, Object> rootInfo = new HashMap<>();
                    rootInfo.put("save_path", subDir.getName() + "/");
                    rootInfo.put("space", 0);
                    rootInfo.put("children", new ArrayList<>()); // Initialize empty children list
                    rootInfo.put("uname", uname);

                    printFileDirectoryTree(subDir, rootInfo, 1);

                    response = rootInfo;
                    break; // Only process the first matching subdirectory
                }
            }
        }
        System.out.println(response);
        return ResponseEntity.ok(response);
    }
    private void printFileDirectoryTree(File directory, Map<String, Object> parentInfo, int depth) {
        File[] fileList = directory.listFiles();
        if (fileList != null) {
            List<Map<String, Object>> children = new ArrayList<>();
            for (File file : fileList) {
                if(file.isDirectory()) {
                    Map<String, Object> fileInfo = new HashMap<>();
                    String savePath =  file.getName() + "/" ;
                    fileInfo.put("save_path", savePath);
                    fileInfo.put("space", depth * 2 - 1); // Customize the space value as needed
                    if (!file.isDirectory()) {
                        fileInfo.put("file_type", getFileType(file.getName()));
                    }
                    children.add(fileInfo);
                    if (file.isDirectory()) {
                        fileInfo.put("children", new ArrayList<>()); // Initialize empty children list
                        printFileDirectoryTree(file, fileInfo, depth + 1);
                    }
                }
            }
            parentInfo.put("children", children);
        }
    }
    @PostMapping("/delete")
    public Result<String> Delete(@RequestBody List<String> paths) {
//        String savePath = pathService.findAll().get(0).getSave_path();
//        System.out.println(savePath);
        String getPath=pathService.findAll().get(0).getSave_path()+"/";
        List<String> formattedPaths = new ArrayList<>(); // 用于存放修正后的路径
        for (String path : paths) {
            // 将路径中的 "easypan" 替换为 "E:/easypan"
            String formattedPath = getPath+path;
            // 将路径中的反斜杠替换为正斜杠
            formattedPath = formattedPath.replace("\\", "/");
            formattedPaths.add(formattedPath);
        }
        System.out.println(formattedPaths);
        try {
            for (String path : formattedPaths) {
                Path folderPath = Paths.get(path);

                if (Files.exists(folderPath)) {
                    if (Files.isDirectory(folderPath)) {
                        FileUtils.deleteDirectory(folderPath.toFile());
                    } else {
                        Files.delete(folderPath);
                    }
                } else {
                    return Result.error("路径不存在", "指定的路径不存在：" + path);
                }
            }
            return Result.success("删除成功", "文件或文件夹删除成功！");
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("删除失败", "发生错误：" + e.getMessage());
        }
    }

    @PostMapping("/download")
    public ResponseEntity<Map<String, List<String>>> downloadFiles(@RequestBody List<String> paths) throws IOException {
        List<String> formattedPaths = new ArrayList<>();
        String getPath=pathService.findAll().get(0).getSave_path()+"/";
        for (String path : paths) {
            System.out.println(path);
            String formattedPath = getPath+path;
            formattedPath = formattedPath.replace("//", "/");
            formattedPaths.add(formattedPath);
        }
        List<String> downloadUrls = new ArrayList<>();
        for (String path : formattedPaths) {
            System.out.println("路径："+path);
            File file = new File(path);
            if (file.isFile()) {
                String fileName = file.getName();
                path=path.replace(getPath,"");
                String[] splitPath=path.split("/");
                System.out.println("路径切割："+ Arrays.toString(splitPath)+splitPath.length);
                StringBuilder truePath= new StringBuilder();
                if(splitPath.length>1){
                    for(int i=0;i<splitPath.length-1;i++){
                        truePath.append(splitPath[i]).append("/");
                    }
                }
                System.out.println("文件名："+fileName);
                System.out.println("路径："+truePath);
                String downloadUrl = "http://localhost:8001/download/"+truePath+fileName;
                downloadUrls.add(downloadUrl);
            }
        }
        Map<String, List<String>> responseData = new HashMap<>();
        responseData.put("downloadUrls", downloadUrls);

        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/save")
    public Result<String> save(@RequestBody Map<String, String> data) {
        String path = data.get("newPath");
        String name = data.get("name");
        String getPath=pathService.findAll().get(0).getSave_path()+"/";
        String formattedPath = getPath+path;
        formattedPath = formattedPath.replace("/", "\\");

        // 创建新文件夹的完整路径
        String newFolderPath = formattedPath + "\\" + name;

        try {
            // 使用 Files 创建新文件夹
            Path folderPath = Paths.get(newFolderPath);
            Files.createDirectories(folderPath); // 递归创建目录

            System.out.println("新文件夹已创建：" + newFolderPath);
            return Result.success("成功", "文件夹创建成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("失败", "文件夹创建失败：" + e.getMessage());
        }
    }
    @PostMapping("/upload")
    public Result<String> uploadChunk(@RequestParam("file") MultipartFile chunk,
                                      @RequestParam("savePath") String savePath,
                                      @RequestParam("chunkIndex") int chunkIndex,
                                      @RequestParam("totalChunks") int totalChunks,
                                      @RequestParam("fileName") String fileName) {
        try {
            // 获取文件保存的基础路径
            String basePath = pathService.findAll().get(0).getSave_path() + "/";
            // 格式化保存路径，将斜杠替换为反斜杠
            String formattedPath = basePath + savePath;
            formattedPath = formattedPath.replace("/", "\\");
            // 构建当前切片的文件名
            String chunkFileName = fileName + ".part" + chunkIndex;
            // 构建目标文件的完整路径
            String targetFilePath = Paths.get(formattedPath, chunkFileName).toString();
            // 创建目标文件
            File targetFile = new File(targetFilePath);
            // 将切片文件内容写入目标文件
            chunk.transferTo(targetFile);
            if (chunkIndex == totalChunks - 1) {
                // 所有切片上传完成，合并它们成为最终文件
                File finalFile = new File(Paths.get(formattedPath, fileName).toString());
                FileOutputStream outputStream = new FileOutputStream(finalFile, true);
                for (int i = 0; i < totalChunks; i++) {
                    String partFileName = fileName + ".part" + i;
                    File partFile = new File(Paths.get(formattedPath, partFileName).toString());
                    FileInputStream inputStream = new FileInputStream(partFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    inputStream.close();
                    partFile.delete(); // 删除切片文件
                }
                outputStream.close();
                // 返回成功消息
                return Result.success("成功", "文件上传成功");
            }
            // 返回切片上传成功消息
            return Result.success("成功", "切片上传成功");
        } catch (IOException e) {
            e.printStackTrace();
            // 返回失败消息
            return Result.error("失败", "文件切片上传失败：" + e.getMessage());
        }
    }

//    @GetMapping("/download/{filePath}")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String filePath, @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
//        String fullPath = pathService.findAll().get(0).getSave_path() + "/" + filePath;
//        File file = new File(fullPath);
//
//        if (!file.exists() || !file.isFile()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        long fileLength = file.length();
//
//        List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
//
//        if (ranges.isEmpty() || ranges.size() > 1) {
//            // Full file download
//            return ResponseEntity
//                    .status(HttpStatus.PARTIAL_CONTENT)
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .contentLength(fileLength)
//                    .body((Resource) new FileSystemResource(file));
//        }
//
//        HttpRange range = ranges.get(0);
//        long start = range.getRangeStart(fileLength);
//        long end = range.getRangeEnd(fileLength);
//
//        long contentLength = end - start + 1;
//
//        Path path = Paths.get(fullPath);
//        Resource resource = (Resource) new InputStreamResource(Files.newInputStream(path));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Accept-Ranges", "bytes");
//        headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
//        headers.setContentLength(contentLength);
//
//        return new ResponseEntity<>(resource, headers, HttpStatus.PARTIAL_CONTENT);
//    }
    @PostMapping("preView")
    public ResponseEntity<Map<String, List<String>>> preViewFiles(@RequestBody List<String> paths) throws IOException {
        List<String> formattedPaths = new ArrayList<>();
        String getPath=pathService.findAll().get(0).getSave_path()+"/";
        for (String path : paths) {
            System.out.println(path);
            String formattedPath = getPath+path;
            formattedPath = formattedPath.replace("//", "/");
            formattedPaths.add(formattedPath);
        }
        List<String> preViewUrls = new ArrayList<>();
        for (String path : formattedPaths) {
            System.out.println("路径："+path);
            File file = new File(path);
            if (file.isFile()) {
                String fileName = file.getName();
                path=path.replace(getPath,"");
                String[] splitPath=path.split("/");
                System.out.println("路径切割："+ Arrays.toString(splitPath)+splitPath.length);
                StringBuilder truePath= new StringBuilder();
                if(splitPath.length>1){
                    for(int i=0;i<splitPath.length-1;i++){
                        truePath.append(splitPath[i]).append("/");
                    }
                }
                System.out.println("文件名："+fileName);
                System.out.println("路径："+truePath);
                String preViewUrl = "http://localhost:8001/preView/"+truePath+fileName;
                preViewUrls.add(preViewUrl);
            }
        }

        Map<String, List<String>> responseData = new HashMap<>();
        responseData.put("preViewUrls", preViewUrls);

        return ResponseEntity.ok(responseData);
    }
}
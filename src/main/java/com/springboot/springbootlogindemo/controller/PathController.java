package com.springboot.springbootlogindemo.controller;

import com.springboot.springbootlogindemo.domain.SavePath;
import com.springboot.springbootlogindemo.domain.User;
import com.springboot.springbootlogindemo.service.PathService;
import com.springboot.springbootlogindemo.service.serviceImpl.utils.Result;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
                    rootInfo.put("children", new ArrayList<>()); // Initialize empty children list
                    rootInfo.put("uname", uname);

                    printDirectoryTree(subDir, rootInfo, 1);

                    response = rootInfo;
                    break; // Only process the first matching subdirectory
                }
            }
        }

        System.out.println(response);
        return ResponseEntity.ok(response);
    }





    private void printDirectoryTree(File directory, Map<String, Object> parentInfo, int depth) {
        File[] fileList = directory.listFiles();
        if (fileList != null) {
            List<Map<String, Object>> children = new ArrayList<>();
            for (File file : fileList) {
                Map<String, Object> fileInfo = new HashMap<>();
                String savePath = file.isDirectory() ? file.getName() + "/" : file.getName();
                fileInfo.put("save_path", savePath);
                fileInfo.put("space", depth * 2 - 1); // Customize the space value as needed

                if (!file.isDirectory()) {
                    fileInfo.put("file_type", getFileType(file.getName()));
                }

                children.add(fileInfo);

                if (file.isDirectory()) {
                    fileInfo.put("children", new ArrayList<>()); // Initialize empty children list
                    printDirectoryTree(file, fileInfo, depth + 1);
                }
            }
            parentInfo.put("children", children);
        }
    }

    private String getFileType(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
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
            String getPath=pathService.findAll().get(0).getSave_path()+"/";
            String formattedPath = getPath+savePath;
            formattedPath = formattedPath.replace("/", "\\");

            String chunkFileName = fileName + ".part" + chunkIndex;
            String targetFilePath = Paths.get(formattedPath, chunkFileName).toString();

            File targetFile = new File(targetFilePath);
            chunk.transferTo(targetFile);

            if (chunkIndex == totalChunks - 1) {
                // All chunks uploaded, combine them into the final file
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

                    partFile.delete(); // Delete the chunk file
                }

                outputStream.close();

                return Result.success("成功", "文件上传成功");
            }

            return Result.success("成功", "切片上传成功");
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("失败", "文件切片上传失败：" + e.getMessage());
        }
    }


}
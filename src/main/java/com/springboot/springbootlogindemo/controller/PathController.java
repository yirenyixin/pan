package com.springboot.springbootlogindemo.controller;

import com.springboot.springbootlogindemo.domain.SavePath;
import com.springboot.springbootlogindemo.domain.User;
import com.springboot.springbootlogindemo.service.PathService;
import com.springboot.springbootlogindemo.service.serviceImpl.utils.Result;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/path")
public class PathController {
    @Resource
    private PathService pathService;

    @PostMapping("/get")
    public ResponseEntity<List<Map<String, Object>>> getController(){

        List<SavePath> paths = pathService.findAll();
        List<Map<String, Object>> files = new ArrayList<>();

        if (!paths.isEmpty()) {
            String rootPath = paths.get(0).getSave_path(); // Assuming the first path is the root directory
            File rootDirectory = new File(rootPath);
            Map<String, Object> rootInfo = new HashMap<>();
            rootInfo.put("save_path", rootDirectory.getName() + "/");
            rootInfo.put("space", 0);
            rootInfo.put("children", new ArrayList<>()); // Initialize empty children list
            files.add(rootInfo);

            printDirectoryTree(rootDirectory, rootInfo, 1);
        }
        System.out.println(files);
        return ResponseEntity.ok(files);
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
        List<String> formattedPaths = new ArrayList<>(); // 用于存放修正后的路径
        for (String path : paths) {
            // 将路径中的 "easypan" 替换为 "E:/easypan"
            String formattedPath = path.replace("easypan", "E:/easypan");
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
    public void downloadFiles(HttpServletResponse response, @RequestBody List<String> paths) {
        try {
            response.setContentType("application/octet-stream");

            int bufferLength = 1024;
            int delayMillis = (int) Math.ceil(1000.0 * bufferLength / 500.0);
            byte[] buffer = new byte[bufferLength];

            for (String path : paths) {
                File file = new File("E:/easypan", path);

                if (file.isFile()) {
                    // 如果是文件，直接返回文件的内容
                    FileInputStream in = new FileInputStream(file);
                    String fileName = file.getName();

                    // 设置响应头，指定文件名，告诉浏览器以附件形式下载
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

                    OutputStream out = response.getOutputStream();

                    int bytesRead;
                    long startTime = System.currentTimeMillis();

                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);

                        long currentTime = System.currentTimeMillis();
                        long elapsedTime = currentTime - startTime;

                        if (elapsedTime < delayMillis) {
                            Thread.sleep(delayMillis - elapsedTime);
                        }

                        startTime = System.currentTimeMillis();
                    }

                    in.close();
                    out.flush();
                    out.close();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/save")
    public Result<String> save(@RequestBody Map<String, String> data) {
        String path = data.get("newPath");
        String name = data.get("name");

        // 根据操作系统不同，将路径分隔符进行适当转换
        String formattedPath = path.replace("easypan", "E:/easypan");
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
//            String fileName = StringUtils.cleanPath(chunk.getOriginalFilename());
            String formattedPath = savePath.replace("easypan", "E:/easypan");
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
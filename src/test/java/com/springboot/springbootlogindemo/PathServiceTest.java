package com.springboot.springbootlogindemo;

import com.springboot.springbootlogindemo.domain.SavePath;
import com.springboot.springbootlogindemo.service.PathService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PathServiceTest {

    @Autowired
    private PathService pathService;

    @Test
    public void testGet() {
        List<SavePath> paths = pathService.findAll();
        System.out.println(paths.size());
        System.out.println(paths.get(0).getSave_path());
        if (!paths.isEmpty()) {
            String rootPath = paths.get(0).getSave_path(); // Assuming the first path is the root directory
            File rootDirectory = new File(rootPath);
            printDirectoryTree(rootDirectory, 0);
        }
    }

    private void printDirectoryTree(File directory, int depth) {
        if (directory.isDirectory()) {
            System.out.println(getIndent(depth) + directory.getName() + "/");
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    printDirectoryTree(file, depth + 1);
                }
            }
        } else {
            System.out.println(getIndent(depth) + directory.getName());
        }
    }

    private String getIndent(int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }
        return indent.toString();
    }
}

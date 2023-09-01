package com.springboot.springbootlogindemo;

import com.springboot.springbootlogindemo.domain.Share;
import com.springboot.springbootlogindemo.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@SpringBootApplication
@EnableScheduling // 启用定时任务支持
public class SpringbootLoginDemoApplication extends SpringBootServletInitializer {
    @Autowired
    private ShareService shareService; // 假设你的 ShareService 是通过自动注入的方式获得的

    public static void main(String[] args) {
        SpringApplication.run(SpringbootLoginDemoApplication.class, args);
    }
    @Scheduled(cron = "0 0 0 * * ?") // 每天晚上12点运行定时任务 秒 分 时
    public void delectShares() {
        List<Share> shares=shareService.findAll();
        LocalDate currentDate = LocalDate.now();
        for(Share share:shares){
            LocalDate endDate = LocalDate.parse(share.getEnd_time(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if(currentDate.isAfter(endDate)){
                shareService.deleteShare(share.getId());
                System.out.println("删除的id："+share.getId());
            }
        }
    }


}

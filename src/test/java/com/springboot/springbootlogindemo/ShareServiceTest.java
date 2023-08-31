package com.springboot.springbootlogindemo;

import com.springboot.springbootlogindemo.domain.SavePath;
import com.springboot.springbootlogindemo.domain.Share;
import com.springboot.springbootlogindemo.service.PathService;
import com.springboot.springbootlogindemo.service.ShareService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.io.File;
import java.util.List;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class ShareServiceTest {

    @Autowired
    private ShareService shareService;

    @Test
    public void testGet() {
        List<Share> shares=shareService.findAll();
        for(Share share:shares){
            System.out.println(share.toString());
        }
    }
    @Test
    public void testGetOne() {
        Share share=shareService.findById("1");
        System.out.println(share.toString());
    }
    @Test
    public void testSave() {
        Share share=new Share();
        share.setId("test");
        share.setPwd("test");
        share.setDetail_path("test");
        share.setSave_path("test");
        share.setCreat_time("test");
        share.setEnd_time("test");
        shareService.saveShare(share);
    }
    @Test
    @Transactional
    public void testDelete() {
        shareService.deleteById("test");
    }
}


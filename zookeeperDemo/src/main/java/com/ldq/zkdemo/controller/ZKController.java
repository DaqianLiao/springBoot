package com.ldq.zkdemo.controller;

import com.ldq.zkdemo.config.ZKConfig;
import com.ldq.zkdemo.dao.ZKClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZKController {
    @Autowired
    private ZKConfig zkConfig;

    @Autowired
    private ZKClient client;

    @RequestMapping("/")
    public String print() {
        System.out.println(zkConfig);
        return zkConfig.toString();
    }

    @RequestMapping("/getData")
    public String getData(String path){
        return client.getNodeData(path);
    }

}

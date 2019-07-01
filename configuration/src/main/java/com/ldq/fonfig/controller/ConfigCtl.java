package com.ldq.fonfig.controller;

import com.ldq.fonfig.entity.ConfigFileScan;
import com.ldq.fonfig.entity.PersonConfigFile;
import com.ldq.fonfig.entity.PersonPrefix;
import com.ldq.fonfig.entity.PersonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigCtl {

    @Autowired
    private PersonValue personValue;

    @Autowired
    private PersonPrefix personPrefix;

    @Autowired
    private PersonConfigFile personConfigFile;

    @Autowired
    private ConfigFileScan configFileScan;

    @RequestMapping("/value")
    public String valuePrint (){

        return personValue.toString();
    }

    @RequestMapping("/prefix")
    public String prefixPring (){
        return personPrefix.toString();
    }

    @RequestMapping("/file")
    public String configFile (){
        return personConfigFile.toString();
    }

    @RequestMapping("/fileScan")
    public String configFileProperties (){
        return configFileScan.toString();
    }
}

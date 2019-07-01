package com.ldq.fonfig.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "config")
@PropertySource("classpath:persion.properties")
public class PersonConfigFile {
    private String name;
    private int age;
}

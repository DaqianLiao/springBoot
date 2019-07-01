package com.ldq.fonfig.entity;

import lombok.Data;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Data
@Component
@PropertySource("classpath:persion.properties")
public class ConfigFileScan {
    private Properties properties;
}

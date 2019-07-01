package com.ldq.fonfig.entity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClassProperties {

    /**
     * 通过当前类反射获取classloader，读取resource下指定的资源
      * @throws IOException
     */
    public static void getProperties() throws IOException {
        String path = "persion.properties";
        InputStream resource = ClassProperties.class.getClassLoader().getResourceAsStream(path);

        Properties properties = new Properties();
        properties.load(resource);
        System.out.println(properties);
    }

    public static void main(String[] args) throws IOException {
        getProperties();

    }
}

package com.ldq.fonfig.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class PersonValue {

    @Value("${person.name}")
    private String name;
    @Value("${person.age}")
    private int age;

}

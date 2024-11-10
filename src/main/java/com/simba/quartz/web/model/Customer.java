package com.simba.quartz.web.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Customer implements Payer {
    private Integer id;
    private String name;
    private String email;
    private String phone;
}

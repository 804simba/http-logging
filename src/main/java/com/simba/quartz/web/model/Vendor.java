package com.simba.quartz.web.model;

import lombok.Data;

@Data
public class Vendor implements Payer {
    private Integer id;
    private String name;
    private String businessName;
}

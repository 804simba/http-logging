package com.simba.quartz.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Payload implements Serializable {

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty("emailAddress")
    private String emailAddress;

    @JsonProperty
    private String phoneNumber;

    @JsonProperty("token")
    private String token;
}

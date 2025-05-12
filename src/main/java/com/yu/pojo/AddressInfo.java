package com.yu.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Setter;

@Data
public class AddressInfo {

    private Integer id;
    @JsonIgnore
    private Integer userId;
    private String recipient;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String address;
    private String zipcode;
    @Setter
    @JsonProperty("isDefault")
    private boolean isDefault;

}

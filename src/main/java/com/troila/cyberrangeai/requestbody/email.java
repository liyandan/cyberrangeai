package com.troila.cyberrangeai.requestbody;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class email implements Serializable {

    @JsonProperty(value = "fullname")
    private String fullname;

    @NotNull(message= "用户的邮箱地址不能为空")
    @JsonProperty(value = "email")
    private String mailaccount;

}

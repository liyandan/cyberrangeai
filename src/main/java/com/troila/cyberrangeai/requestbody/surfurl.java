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
public class surfurl implements Serializable {

    @NotNull(message= "浏览的网页url不能为空")
    @JsonProperty(value = "url")
    private String url;
}

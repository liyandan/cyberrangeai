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
public class action implements Serializable {

    @NotNull(message= "用户行为类型不能为空")
    @JsonProperty(value = "action")
    private String action;

    @NotNull(message= "用户行为类型的权重不能为空")
    @JsonProperty(value = "weight")
    private float weight;
}

package com.troila.cyberrangeai.requestbody;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CapabilityBody implements Serializable {

    @NotNull(message= "网络场景ID不能为空")
    @JsonProperty(value = "scenario_id")
    private String  scenario_id;

    @NotNull(message = "用户ID不能为空")
    @JsonProperty(value = "user_id")
    private String  user_id;

    @NotNull(message = "用户邮箱不能为空")
    @JsonProperty(value = "user_email")
    private String  user_email;

    @JsonProperty(value = "capibilities")
    private List<action>  capibilities;

    @JsonProperty(value = "surfing_url_list")
    private List<surfurl> surfing_url_list;

    @JsonProperty(value = "email_to_list")
    private List<email> email_to_list;
}

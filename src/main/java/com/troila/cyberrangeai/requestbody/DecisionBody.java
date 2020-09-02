package com.troila.cyberrangeai.requestbody;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sun.util.resources.cldr.gsw.LocaleNames_gsw;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DecisionBody implements Serializable {

    @JsonProperty(value = "user_id")
    private String user_id;

    @JsonProperty(value = "scenario_id")
    private String scenario_id;

    @JsonProperty(value = "events")
    List<event>   events;

    @JsonProperty(value = "stat")
    private BigInteger stat;

}

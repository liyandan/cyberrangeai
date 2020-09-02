package com.troila.cyberrangeai.requestbody;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class event implements Serializable {

    @JsonProperty(value = "event")
    private String eventname;

    @JsonProperty(value = "data_received_email")
    private receivedemailinfo data_received_email;
}

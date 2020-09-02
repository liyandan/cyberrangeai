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
public class receivedemailinfo implements Serializable {

    @JsonProperty(value = "from")
    private String from;

    @JsonProperty(value = "cc")
    private String cc;

    @JsonProperty(value = "subject")
    private String subject;

    @JsonProperty(value = "body")
    private String body;
}

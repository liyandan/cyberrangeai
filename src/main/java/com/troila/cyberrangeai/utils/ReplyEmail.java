package com.troila.cyberrangeai.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReplyEmail {

        private String to;
        private String cc;
        private String subject;
        private String body;
}

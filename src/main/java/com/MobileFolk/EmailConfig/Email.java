package com.MobileFolk.EmailConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    private String host;
    private String port;
    private String username;
    private String password;
    private String from;
    private String[] recipients;


}


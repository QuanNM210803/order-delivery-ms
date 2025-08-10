package com.odms.socket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.odms.socket",
        "nmquan.commonlib"
})
public class SocketServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketServiceApplication.class, args);
    }

}

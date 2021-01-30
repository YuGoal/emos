package io.caoyu.emos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class EmosApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmosApplication.class, args);
    }

}

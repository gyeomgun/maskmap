package com.hg.project.maskmap;

import com.hg.project.maskmap.domain.config.DomainConfig;
import com.hg.project.maskmap.web.config.WebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({WebConfig.class, DomainConfig.class})
public class MaskMapWebApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MaskMapWebApplication.class, args);
    }
}

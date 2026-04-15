package com.eapple;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * web瀹瑰櫒涓繘琛岄儴缃?
 * 
 * @author Eapp1e
 */
public class EduPlatformServletInitializer extends SpringBootServletInitializer
{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        return application.sources(EduPlatformApplication.class);
    }
}


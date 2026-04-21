package com.eapple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

/**
 * EAPPLE education platform launcher.
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class EduPlatformApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EduPlatformApplication.class, args);
        System.out.println(
                "\n" +
                "  EEEEE    A    PPPP   PPPP   L      EEEEE\n" +
                "  E       A A   P   P  P   P  L      E    \n" +
                "  EEEE   AAAAA  PPPP   PPPP   L      EEEE \n" +
                "  E      A   A  P      P      L      E    \n" +
                "  EEEEE  A   A  P      P      LLLLL  EEEEE\n" +
                "\n" +
                "  platform started successfully.\n");
    }
}

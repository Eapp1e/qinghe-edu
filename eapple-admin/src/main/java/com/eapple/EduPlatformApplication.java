package com.eapple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

/**
 * QINGHE After-school Service Platform launcher.
 *
 * @author EAPPLE
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class EduPlatformApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EduPlatformApplication.class, args);
        System.out.println(
                "\n" +
                "   ____    ___   _   _   ____   _   _   _____\n" +
                "  / __ \\  |_ _| | \\ | | / ___| | | | | | ____|\n" +
                " | |  | |  | |  |  \\| || |  _  | |_| | |  _|\n" +
                " | |__| |  | |  | |\\  || |_| | |  _  | | |___\n" +
                "  \\___\\_\\ |___| |_| \\_| \\____| |_| |_| |_____|\n" +
                "\n" +
                "  QINGHE After-school Service Platform started successfully.\n");
    }
}

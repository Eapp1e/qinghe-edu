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
                "      ______      ______      ______      ______      ______      ______\n" +
                "     /\\  __ \\    /\\  __ \\    /\\  __ \\    /\\  ___\\    /\\  __ \\    /\\  ___\\\n" +
                "     \\ \\ \\/\\_\\   \\ \\  __ \\   \\ \\  __ \\   \\ \\ \\__ \\   \\ \\ \\_\\ \\   \\ \\  __\\\n" +
                "      \\ \\___\\_\\   \\ \\_\\ \\_\\   \\ \\_\\ \\_\\   \\ \\_____\\   \\ \\_____\\   \\ \\_____\\\n" +
                "       \\/___/_/    \\/_/\\/_/    \\/_/\\/_/    \\/_____/    \\/_____/    \\/_____/\n" +
                "\n" +
                "  QINGHE After-school Service Platform started successfully.\n" +
                "  GitHub: @Eapp1e\n");
    }
}

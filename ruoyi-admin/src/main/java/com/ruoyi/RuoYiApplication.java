package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

/**
 * 平台启动程序
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RuoYiApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(RuoYiApplication.class, args);
        System.out.println("(#^.^#)  中小学智能课后服务平台启动成功\n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /      \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'        \n" +
                " | (_,_).' __  ___(_ o _)'         \n" +
                " |  |\\ \\  |  ||   |(_,_)'          \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /            \n" +
                " ''-'   `'-'    `-..-'             ");
    }
}

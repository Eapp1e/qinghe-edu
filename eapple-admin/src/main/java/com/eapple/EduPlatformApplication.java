package com.eapple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

/**
 * 骞冲彴鍚姩绋嬪簭
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class EduPlatformApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EduPlatformApplication.class, args);
        System.out.println("(#^.^#)  涓皬瀛︽櫤鑳借鍚庢湇鍔″钩鍙板惎鍔ㄦ垚鍔焅n" +
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


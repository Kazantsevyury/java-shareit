package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ShareItAppMainTest  {

    @Test
    public void contextLoads() {
        ShareItApp.main(new String[]{"--spring.main.banner-mode=off"});
    }
}
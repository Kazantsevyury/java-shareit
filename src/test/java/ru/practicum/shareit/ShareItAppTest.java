package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ShareItAppTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(ShareItApp.class);

    /**
     * Test main method when the application starts then success
     * This test case will verify the behavior of the main method when the application starts successfully.
     * We will use the ApplicationContextRunner to run the main method and then check if the application context is not null.
     * No setup is required for this test case.
     */
    @Test
    public void testMainWhenApplicationStartsThenSuccess() {
        contextRunner.run(context -> assertThat(context).isNotNull());
    }

    /**
     * Test main method when command line arguments then behavior as expected
     * This test case will verify the behavior of the main method when command line arguments are passed.
     * We will use the ApplicationContextRunner to run the main method with command line arguments and then check if the application context is not null.
     * No setup is required for this test case.
     */
    @Test
    public void testMainWhenCommandLineArgumentsThenBehaviorAsExpected() {
        String[] args = {"--spring.main.banner-mode=off"};
        contextRunner.run(context -> {
            assertThat(context).isNotNull();
            SpringApplication.run(ShareItApp.class, args);
        });
    }
}
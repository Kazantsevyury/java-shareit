
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ru.practicum.shareit.ShareItApp;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItApp.class)
public class ShareItAppTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void contextLoads() throws Exception {
        assertThat(context).isNotNull();
    }
}


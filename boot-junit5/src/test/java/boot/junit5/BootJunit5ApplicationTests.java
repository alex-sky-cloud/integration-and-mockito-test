package boot.junit5;

import boot.junit5.test.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

//@ActiveProfiles("integrationtest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BootJunit5ApplicationTests extends AbstractIntegrationTest {




    @Test
    public void checkContainer(){
        boolean created =   getContainerMariaDb().isCreated();
        System.out.println(created);

        boolean running =   getContainerMariaDb().isRunning();
        System.out.println(running);
    }

}

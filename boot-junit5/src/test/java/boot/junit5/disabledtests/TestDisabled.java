package boot.junit5.disabledtests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDisabled {

    @Test
    @DisabledOnOs(OS.LINUX)
    public void disabledOnLinux() {
        assertEquals(42, 40 + 2);
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "FLAKY_TESTS", matches = "false")
    public void disableFlakyTest() {
        assertEquals(42, 40 + 2);
    }


    @Test
    @DisabledOnMidnight
    public void disabledOnMidNight() {
        assertEquals(42, 40 + 2);
    }

}

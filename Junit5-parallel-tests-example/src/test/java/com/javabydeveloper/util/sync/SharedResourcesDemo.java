package com.javabydeveloper.util.sync;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.Properties;

@Disabled
@Execution(ExecutionMode.CONCURRENT)
public class SharedResourcesDemo {

	private Properties backup;

    @BeforeEach
    void backup() {
        backup = new Properties();
        backup.putAll(System.getProperties());
    }

    @AfterEach
    void restore() {
        System.setProperties(backup);
    }

    @Test
    @ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ)
    void customPropertyIsNotSetByDefault() {
    	System.out.println("customPropertyIsNotSetByDefault=> "+System.getProperty("my.prop"));
        Assertions.assertNull(System.getProperty("my.prop"));
    }

    @Test
    @ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
    void canSetCustomPropertyToApple() {
    	
        System.setProperty("my.prop", "apple");
        System.out.println("canSetCustomPropertyToApple=> "+System.getProperty("my.prop"));
        Assertions.assertEquals("apple", System.getProperty("my.prop"));
    }

    @Test
    @ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
    void canSetCustomPropertyToBanana() {
        System.setProperty("my.prop", "banana");
        System.out.println("canSetCustomPropertyToBanana=> "+System.getProperty("my.prop"));
        Assertions.assertEquals("banana", System.getProperty("my.prop"));
    }
}

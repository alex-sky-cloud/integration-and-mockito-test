package boot.junit5.disabledtests;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DisabledOnMidnightCondition.class)
public @interface DisabledOnMidnight {

}

package ru.raiffeisen.rmcp.id;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import ru.raiffeisen.rmcp.id.config.OAuthDbInitializerConfiguration;
import ru.raiffeisen.rmcp.id.config.R2dbcOAuthDbConfiguration;
import ru.raiffeisen.rmcp.id.config.R2dbcUserDbConfiguration;
import ru.raiffeisen.rmcp.id.config.UserDbInitializerConfiguration;
import ru.raiffeisen.rmcp.id.config.initializer.PostgreSQLContainerInitializer;

@DataR2dbcTest
@ContextConfiguration(
        initializers = PostgreSQLContainerInitializer.class,
        classes = {
                R2dbcOAuthBaseDbConfiguration.class,
                R2dbcUserBaseDbConfiguration.class,
                OAuthBaseDbInitializerConfiguration.class,
                UserDbBaseInitializerConfiguration.class
        }
)
@ComponentScan(
        includeFilters = @ComponentScan.Filter(value = Repository.class),
        useDefaultFilters = false
)
@AutoConfigurationPackage(basePackageClasses = Application.class)
public abstract class AbstractTestRepositoryTests {
}

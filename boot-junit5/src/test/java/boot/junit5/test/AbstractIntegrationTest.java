package boot.junit5.test;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;


public abstract class AbstractIntegrationTest {

    private static final DockerImageName MARIADB_IMAGE = DockerImageName.parse("mariadb");
    private static final MariaDBContainer mariadb;

    /*Не работает*
    java.lang.IllegalStateException: Failed to process
    JAR file when extracting classpath resource:
    jar:file:/D:/!_apache-mvn-local-repo/org/testcontainers/mariadb/1.15.1/mariadb-1.15.1.jar!/mariadb-default-conf
     */
    static {

        mariadb = new MariaDBContainer<>(MARIADB_IMAGE)
            .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci");
        mariadb.start();

        boolean created = mariadb.isCreated();
        System.out.println(created);

        boolean running = mariadb.isRunning();

        System.out.println(running);
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {

        String databaseName = mariadb.getDatabaseName();
        String jdbcUrl = mariadb.getJdbcUrl();
        String username = mariadb.getUsername();
        String password = mariadb.getPassword();

        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
    }

    public static MariaDBContainer getContainerMariaDb() {
        return mariadb;
    }



}

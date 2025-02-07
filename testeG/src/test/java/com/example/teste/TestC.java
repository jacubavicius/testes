import static org.mockito.Mockito.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.zaxxer.hikari.HikariDataSource;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class DataSourceCustomConfigTest {

    @InjectMocks
    private DataSourceCustomConfig dataSourceCustomConfig;

    @Mock
    private PwMatrixUtil pwMatrixUtil;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.security.pwMatrix-server}")
    private String pwMatrixServer;

    @Value("${spring.security.pwMatrix-user-id}")
    private String pwMatrixUserId;

    @Test
    void testDataSource() throws Exception {
        when(pwMatrixUtil.getPassword(pwMatrixServer, pwMatrixUserId)).thenReturn("mockedPassword");

        DataSource dataSource = dataSourceCustomConfig.datasource();

        assertNotNull(dataSource);
        assertTrue(dataSource instanceof HikariDataSource);
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        assertEquals(url, hikariDataSource.getJdbcUrl());
        assertEquals(username, hikariDataSource.getUsername());
        assertEquals(driverClassName, hikariDataSource.getDriverClassName());
    }
}

//
//<dependency>
//<groupId>org.springframework.boot</groupId>
//<artifactId>spring-boot-starter-test</artifactId>
//<scope>test</scope>
//</dependency>
//<dependency>
//<groupId>org.mockito</groupId>
//<artifactId>mockito-core</artifactId>
//<scope>test</scope>
//</dependency>
//<dependency>
//<groupId>com.zaxxer</groupId>
//<artifactId>HikariCP</artifactId>
//<version>5.0.0</version>
//</dependency>
package com.example.teste;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class DataSourceCustomConfigTest {

    @InjectMocks
    private DataSourceCustomConfig dataSourceCustomConfig;

    @Mock
    private Environment environment; // Para mockar as propriedades do application.properties

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Mockando os valores das propriedades
        when(environment.getProperty("spring.datasource.username")).thenReturn("test_user");
        when(environment.getProperty("spring.datasource.password")).thenReturn("test_password");
        when(environment.getProperty("spring.datasource.url")).thenReturn("jdbc:h2:mem:testdb");
        when(environment.getProperty("spring.datasource.driverClassName")).thenReturn("org.h2.Driver");
        when(environment.getProperty("spring.security.datasource.pwMatrix-enabled", Boolean.class, false)).thenReturn(false);
    }

    @Test
    void testDataSourceWithoutPwMatrix() {
        DataSource dataSource = dataSourceCustomConfig.datasource();

        assertNotNull(dataSource);
        assertTrue(dataSource instanceof HikariDataSource);

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        assertEquals("jdbc:h2:mem:testdb", hikariDataSource.getJdbcUrl());
        assertEquals("test_user", hikariDataSource.getUsername());
        assertEquals("test_password", hikariDataSource.getPassword());
    }

    @Test
    void testDataSourceWithPwMatrix() throws Exception {
        // Mockando o comportamento do PwMatrixUtil
        try (MockedStatic<PwMatrixUtil> mockedPwMatrixUtil = mockStatic(PwMatrixUtil.class)) {
            mockedPwMatrixUtil.when(() -> PwMatrixUtil.getPassword("testServer", "testUser"))
                    .thenReturn("mocked_password");

            when(environment.getProperty("spring.security.datasource.pwMatrix-enabled", Boolean.class, false)).thenReturn(true);
            when(environment.getProperty("spring.security.pwMatrix-server")).thenReturn("testServer");
            when(environment.getProperty("spring.security.pwMatrix-user-id")).thenReturn("testUser");

            DataSource dataSource = dataSourceCustomConfig.datasource();

            assertNotNull(dataSource);
            assertTrue(dataSource instanceof HikariDataSource);

            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            assertEquals("jdbc:h2:mem:testdb", hikariDataSource.getJdbcUrl());
            assertEquals("test_user", hikariDataSource.getUsername());
            assertEquals("mocked_password", hikariDataSource.getPassword());
        }
    }
}

package com.techelevator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.io.IOException;
import java.sql.SQLException;

public class TenmoDaoTests {

    public static SingleConnectionDataSource dataSource;

    @BeforeClass
    public static void setupDataSource() {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
        dataSource.setUsername("tenmo");
        dataSource.setPassword("tenmo_appuser");
        dataSource.setAutoCommit(false);
    }

    @Before
    public void loadTestData() throws IOException, SQLException {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("resources/dummydata.sql"));
    }

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    @AfterClass
    public static void closeDataSource() {
        dataSource.destroy();
    }
}

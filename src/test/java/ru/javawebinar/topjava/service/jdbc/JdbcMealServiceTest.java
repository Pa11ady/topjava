package ru.javawebinar.topjava.service.jdbc;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.AbstractMealServiceTest;

import static ru.javawebinar.topjava.Profiles.JDBC;

@ActiveProfiles(JDBC)
public class JdbcMealServiceTest extends AbstractMealServiceTest {
    @Override
    @Ignore("Not supported for JDBC")
    @Test
    public void createWithException() {
    }
}

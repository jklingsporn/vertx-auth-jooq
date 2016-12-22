package io.github.jklingsporn.vertx.auth.jooq;

import io.github.jklingsporn.vertx.auth.jooq.generated.tables.daos.UserDao;
import io.github.jklingsporn.vertx.auth.jooq.generated.tables.daos.UserRolesDao;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by jensklingsporn on 22.12.16.
 */
public class JooqAuthTest extends VertxTestBase{

    @BeforeClass
    public static void createDb() throws Exception {
        TestTool.setupDB();
    }

    protected JooqAuthProvider authProvider;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        authProvider = createProvider();
    }

    protected JooqAuthProvider createProvider() throws SQLException {
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.HSQLDB);
        configuration.set(DriverManager.getConnection("jdbc:hsqldb:mem:test", "test", ""));
        UserDao userDao = new UserDao(configuration);
        userDao.setVertx(vertx);
        UserRolesDao userRolesDao = new UserRolesDao(configuration);
        userRolesDao.setVertx(vertx);
        return JooqAuthProvider.create(userDao,userRolesDao);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testAuthenticate() {
        JsonObject authInfo = new JsonObject();
        authInfo.put("username", "tim").put("password", "sausages");
        authProvider.authenticate(authInfo, onSuccess(user -> {
            assertNotNull(user);
            testComplete();
        }));
        await();
    }

    @Test
    public void testAuthenticateFailBadPwd() {
        JsonObject authInfo = new JsonObject();
        authInfo.put("username", "tim").put("password", "eggs");
        authProvider.authenticate(authInfo, onFailure(v -> {
            assertEquals("Invalid username/password", v.getMessage());
            testComplete();
        }));
        await();
    }

    @Test
    public void testAuthenticateFailBadUser() {
        JsonObject authInfo = new JsonObject();
        authInfo.put("username", "blah").put("password", "whatever");
        authProvider.authenticate(authInfo, onFailure(v -> {
            assertEquals("Invalid username/password", v.getMessage());
            testComplete();
        }));
        await();
    }

    @Test
    public void testAuthoriseHasRole() {
        JsonObject authInfo = new JsonObject();
        authInfo.put("username", "tim").put("password", "sausages");
        authProvider.authenticate(authInfo, onSuccess(user -> {
            assertNotNull(user);
            user.isAuthorised("role:dev", onSuccess(has -> {
                assertTrue(has);
                testComplete();
            }));
        }));
        await();
    }

    @Test
    public void testAuthoriseNotHasRole() {
        JsonObject authInfo = new JsonObject();
        authInfo.put("username", "tim").put("password", "sausages");
        authProvider.authenticate(authInfo, onSuccess(user -> {
            assertNotNull(user);
            user.isAuthorised("role:manager", onSuccess(has -> {
                assertFalse(has);
                testComplete();
            }));
        }));
        await();
    }

    @Test
    public void testAuthoriseHasPermission() {
        JsonObject authInfo = new JsonObject();
        authInfo.put("username", "tim").put("password", "sausages");
        authProvider.authenticate(authInfo, onSuccess(user -> {
            assertNotNull(user);
            user.isAuthorised("commit_code", onSuccess(has -> {
                assertTrue(has);
                testComplete();
            }));
        }));
        await();
    }

    @Test
    public void testAuthoriseNotHasPermission() {
        JsonObject authInfo = new JsonObject();
        authInfo.put("username", "tim").put("password", "sausages");
        authProvider.authenticate(authInfo, onSuccess(user -> {
            assertNotNull(user);
            user.isAuthorised("eat_sandwich", onSuccess(has -> {
                assertFalse(has);
                testComplete();
            }));
        }));
        await();
    }
}

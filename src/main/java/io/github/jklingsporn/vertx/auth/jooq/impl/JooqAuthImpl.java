package io.github.jklingsporn.vertx.auth.jooq.impl;

import io.github.jklingsporn.vertx.auth.jooq.JooqAuthProvider;
import io.github.jklingsporn.vertx.auth.jooq.generated.tables.daos.UserDao;
import io.github.jklingsporn.vertx.auth.jooq.generated.tables.daos.UserRolesDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jensklingsporn on 10.12.16.
 */
public class JooqAuthImpl implements JooqAuthProvider {

    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    private UserDao userDao;
    private UserRolesDao userRolesDao;

    private String algorithm = "SHA-512";

    public JooqAuthImpl setUserRolesDao(UserRolesDao userRolesDao) {
        this.userRolesDao = userRolesDao;
        return this;
    }

    public JooqAuthImpl setUserDao(UserDao userDao) {
        this.userDao = userDao;
        return this;
    }

    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        final String username = authInfo.getString("username");
        if (username == null) {
            resultHandler.handle(Future.failedFuture("authInfo must contain username in 'username' field"));
            return;
        }
        final String password = authInfo.getString("password");
        if (password == null) {
            resultHandler.handle(Future.failedFuture("authInfo must contain password in 'password' field"));
            return;
        }
        userDao.fetchOneByUsernameAsync(username, r -> {
            if (r.succeeded() && r.result()!=null) {
                //since username is PK there is no need to check for multiple entries
                try {
                    String expected = computeHash(password,r.result().getPasswordSalt(),algorithm);
                    if(expected.equalsIgnoreCase(r.result().getPassword())){
                        resultHandler.handle(Future.succeededFuture(new JooqUserImpl(userRolesDao,username,"role:")));
                    }else{
                        resultHandler.handle(Future.failedFuture(new IllegalAccessException("Invalid username/password")));
                    }

                } catch (NoSuchAlgorithmException e) {
                    resultHandler.handle(Future.failedFuture(e));
                }
            } else {
                resultHandler.handle(Future.failedFuture("Invalid username/password"));
            }
        });
    }

    private static String computeHash(String password, String salt, String algo) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algo);
        String concat = (salt == null ? "" : salt) + password;
        byte[] bHash = md.digest(concat.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(bHash);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int x = 0xFF & bytes[i];
            chars[i * 2] = HEX_CHARS[x >>> 4];
            chars[1 + i * 2] = HEX_CHARS[0x0F & x];
        }
        return new String(chars);
    }
}

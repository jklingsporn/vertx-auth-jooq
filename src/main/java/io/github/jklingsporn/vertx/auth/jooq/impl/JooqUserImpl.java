package io.github.jklingsporn.vertx.auth.jooq.impl;

import io.github.jklingsporn.vertx.auth.jooq.JooqUser;
import io.github.jklingsporn.vertx.auth.jooq.generated.Tables;
import io.github.jklingsporn.vertx.auth.jooq.generated.tables.daos.UserRolesDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.impl.DSL;

import java.nio.charset.StandardCharsets;


/**
 * Created by jensklingsporn on 21.12.16.
 */
class JooqUserImpl extends AbstractUser implements JooqUser {

    private final UserRolesDao userRolesDao;
    private String username;
    private String rolePrefix;
    private JsonObject principal;

    JooqUserImpl(UserRolesDao userRolesDao, String username, String rolePrefix) {
        this.userRolesDao = userRolesDao;
        this.username = username;
        this.rolePrefix = rolePrefix;
    }


    @Override
    protected void doIsPermitted(String permissionOrRole, Handler<AsyncResult<Boolean>> resultHandler) {
        if (permissionOrRole != null && permissionOrRole.startsWith(rolePrefix)) {
            this.hasRole(permissionOrRole.substring(rolePrefix.length()), resultHandler);
        } else {
            this.hasPermission(permissionOrRole, resultHandler);
        }
    }

    @Override
    public JsonObject principal() {
        if (principal == null) {
            principal = new JsonObject().put("username", username);
        }
        return principal;
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {
        //dont need, we're using DAOs
    }

    @Override
    public void writeToBuffer(Buffer buff) {
        super.writeToBuffer(buff);
        byte[] bytes = username.getBytes(StandardCharsets.UTF_8);
        buff.appendInt(bytes.length);
        buff.appendBytes(bytes);

        bytes = rolePrefix.getBytes(StandardCharsets.UTF_8);
        buff.appendInt(bytes.length);
        buff.appendBytes(bytes);
    }

    @Override
    public int readFromBuffer(int pos, Buffer buffer) {
        pos = super.readFromBuffer(pos, buffer);
        int len = buffer.getInt(pos);
        pos += 4;
        byte[] bytes = buffer.getBytes(pos, pos + len);
        username = new String(bytes, StandardCharsets.UTF_8);
        pos += len;

        len = buffer.getInt(pos);
        pos += 4;
        bytes = buffer.getBytes(pos, pos + len);
        rolePrefix = new String(bytes, StandardCharsets.UTF_8);
        pos += len;

        return pos;
    }

    protected void hasPermission(String permission, Handler<AsyncResult<Boolean>> resultHandler) {
        userRolesDao.executeAsync(
                dslContext ->
                        dslContext.
                                selectCount().
                                from(Tables.ROLES_PERMS).
                                join(Tables.USER_ROLES).
                                    on(Tables.USER_ROLES.USERNAME.eq(username)).
                                    and(Tables.USER_ROLES.ROLE.eq(Tables.ROLES_PERMS.ROLE)).
                                where(Tables.ROLES_PERMS.PERM.eq(permission)).
                                fetchOne(0, Integer.class) > 0
                , resultHandler);
    }

    protected void hasRole(String role, Handler<AsyncResult<Boolean>> resultHandler) {
        userRolesDao.existsByIdAsync(newRecord(Tables.USER_ROLES.USERNAME, Tables.USER_ROLES.ROLE, username, role), resultHandler);
    }

    private <T1,T2> Record2<T1,T2> newRecord(Field<T1> field1, Field<T2> field2, T1 value1, T2 value2){
        Record2<T1,T2> record = DSL.using(userRolesDao.configuration()).newRecord(field1, field2);
        record.set(field1,value1);
        record.set(field2,value2);
        return record;
    }
}

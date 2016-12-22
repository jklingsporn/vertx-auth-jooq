package io.github.jklingsporn.vertx.auth.jooq;

import io.github.jklingsporn.vertx.auth.jooq.generated.tables.daos.UserDao;
import io.github.jklingsporn.vertx.auth.jooq.generated.tables.daos.UserRolesDao;
import io.github.jklingsporn.vertx.auth.jooq.impl.JooqAuthImpl;
import io.vertx.ext.auth.AuthProvider;

/**
 * Created by jensklingsporn on 10.12.16.
 */
public interface JooqAuthProvider extends AuthProvider {

    static JooqAuthProvider create(UserDao userDao, UserRolesDao userRolesDao){
        return new JooqAuthImpl().setUserDao(userDao).setUserRolesDao(userRolesDao);
    }

}

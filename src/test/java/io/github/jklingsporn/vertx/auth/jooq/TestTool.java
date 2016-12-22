package io.github.jklingsporn.vertx.auth.jooq;

import io.github.jklingsporn.vertx.impl.VertxGenerator;
import io.github.jklingsporn.vertx.impl.VertxGeneratorStrategy;
import org.jooq.util.GenerationTool;
import org.jooq.util.hsqldb.HSQLDBDatabase;
import org.jooq.util.jaxb.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jensklingsporn on 10.12.16.
 */
public class TestTool {

    private static final String TARGET_FOLDER = System.getProperty("user.dir") + "/src/main/java";

    static final List<String> SQL = new ArrayList<>();

    static {
        SQL.add("DROP SCHEMA IF EXISTS auth CASCADE");
        SQL.add("CREATE SCHEMA auth");
        SQL.add("SET SCHEMA auth");
        SQL.add("drop table if exists user;");
        SQL.add("drop table if exists user_roles;");
        SQL.add("drop table if exists roles_perms;");
        SQL.add("create table user (username varchar(255) PRIMARY KEY, password varchar(255), password_salt varchar(255) );");
        SQL.add("create table user_roles (username varchar(255), role varchar(255), PRIMARY KEY (USERNAME,ROLE));");
        SQL.add("create table roles_perms (role varchar(255), perm varchar(255), PRIMARY KEY (ROLE, PERM));");

        SQL.add("insert into user values ('tim', 'EC0D6302E35B7E792DF9DA4A5FE0DB3B90FCAB65A6215215771BF96D498A01DA8234769E1CE8269A105E9112F374FDAB2158E7DA58CDC1348A732351C38E12A0', 'C59EB438D1E24CACA2B1A48BC129348589D49303858E493FBE906A9158B7D5DC');");
        SQL.add("insert into user_roles values ('tim', 'dev');");
        SQL.add("insert into user_roles values ('tim', 'admin');");
        SQL.add("insert into roles_perms values ('dev', 'commit_code');");
        SQL.add("insert into roles_perms values ('dev', 'eat_pizza');");
        SQL.add("insert into roles_perms values ('admin', 'merge_pr');");

        // and a second set of tables with slight differences

//        SQL.add("drop table if exists user2;");
//        SQL.add("drop table if exists user_roles2;");
//        SQL.add("drop table if exists roles_perms2;");
//        SQL.add("create table user2 (user_name varchar(255), pwd varchar(255), pwd_salt varchar(255) );");
//        SQL.add("create table user_roles2 (user_name varchar(255), role varchar(255));");
//        SQL.add("create table roles_perms2 (role varchar(255), perm varchar(255));");
//
//        SQL.add("insert into user2 values ('tim', 'EC0D6302E35B7E792DF9DA4A5FE0DB3B90FCAB65A6215215771BF96D498A01DA8234769E1CE8269A105E9112F374FDAB2158E7DA58CDC1348A732351C38E12A0', 'C59EB438D1E24CACA2B1A48BC129348589D49303858E493FBE906A9158B7D5DC');");
//        SQL.add("insert into user_roles2 values ('tim', 'dev');");
//        SQL.add("insert into user_roles2 values ('tim', 'admin');");
//        SQL.add("insert into roles_perms2 values ('dev', 'commit_code');");
//        SQL.add("insert into roles_perms2 values ('dev', 'eat_pizza');");
//        SQL.add("insert into roles_perms2 values ('admin', 'merge_pr');");

    }

    public static void main(String[] args) throws Exception {
        setupDB();
        Configuration config = createGeneratorConfig(VertxGenerator.class.getName(), "io.github.jklingsporn.vertx.auth.jooq.generated");
        GenerationTool.generate(config);
    }

    static void setupDB() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:test", "test", "");
        for (String sql : SQL) {
            connection.prepareStatement(sql).execute();
        }
    }

    static Configuration createGeneratorConfig(String generatorName, String packageName){

        /*
         * We're using HSQLDB to generate our files
         */
        Configuration configuration = new Configuration();
        Database databaseConfig = new Database();
        databaseConfig.setName(HSQLDBDatabase.class.getName());
        databaseConfig.setInputSchema("AUTH");
        databaseConfig.setOutputSchema("AUTH");

        Target targetConfig = new Target();
        targetConfig.setPackageName(packageName);
        targetConfig.setDirectory(TARGET_FOLDER);

        Generate generateConfig = new Generate();
        /*
         * When you set the interfaces-flag to true (recommended), the fromJson and toJson methods
         * are added as default-methods to the interface (so also jooq.Records will benefit)
         */
        generateConfig.setInterfaces(true);
        generateConfig.setPojos(true);
        generateConfig.setFluentSetters(true);
        generateConfig.setDaos(true);

        /*
         * We need to do a small hack to let jOOQ's DAOImpl implement our interface. That's why
         * we need a custom Strategy.
         */
        Strategy strategy = new Strategy();
        strategy.setName(VertxGeneratorStrategy.class.getName());

        Generator generatorConfig = new Generator();
        generatorConfig.setName(generatorName);
        generatorConfig.setDatabase(databaseConfig);
        generatorConfig.setTarget(targetConfig);
        generatorConfig.setGenerate(generateConfig);
        generatorConfig.setStrategy(strategy);
        configuration.setGenerator(generatorConfig);

        Jdbc jdbcConfig = new Jdbc();
        jdbcConfig.setDriver("org.hsqldb.jdbcDriver");
        jdbcConfig.setUrl("jdbc:hsqldb:mem:test");
        jdbcConfig.setUser("test");
        jdbcConfig.setPassword("");
        configuration.setJdbc(jdbcConfig);

        return configuration;
    }

}

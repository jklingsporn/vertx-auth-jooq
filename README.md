# vertx-auth-jooq
An example of using [vertx-jooq](https://github.com/jklingsporn/vertx-jooq) together with [vertx-auth](http://vertx.io/docs/vertx-auth-common/java/). This implementation was heavily inspired by [vertx-auth-jdbc](http://vertx.io/docs/vertx-auth-jdbc/java/) and uses the same unit-tests to show it is working.

To generate the POJOs and DAOs, this implementation uses HSQLDB. You can trigger the code generation by running the main-method of the `TestTool`.
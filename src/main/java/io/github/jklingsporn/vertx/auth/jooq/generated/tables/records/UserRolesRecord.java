/**
 * This class is generated by jOOQ
 */
package io.github.jklingsporn.vertx.auth.jooq.generated.tables.records;


import io.github.jklingsporn.vertx.auth.jooq.generated.tables.UserRoles;
import io.github.jklingsporn.vertx.auth.jooq.generated.tables.interfaces.IUserRoles;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UserRolesRecord extends UpdatableRecordImpl<UserRolesRecord> implements Record2<String, String>, IUserRoles {

    private static final long serialVersionUID = 1936321976;

    /**
     * Setter for <code>AUTH.USER_ROLES.USERNAME</code>.
     */
    @Override
    public UserRolesRecord setUsername(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>AUTH.USER_ROLES.USERNAME</code>.
     */
    @Override
    public String getUsername() {
        return (String) get(0);
    }

    /**
     * Setter for <code>AUTH.USER_ROLES.ROLE</code>.
     */
    @Override
    public UserRolesRecord setRole(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>AUTH.USER_ROLES.ROLE</code>.
     */
    @Override
    public String getRole() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<String, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<String, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return UserRoles.USER_ROLES.USERNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return UserRoles.USER_ROLES.ROLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getUsername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getRole();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRolesRecord value1(String value) {
        setUsername(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRolesRecord value2(String value) {
        setRole(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRolesRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void from(IUserRoles from) {
        setUsername(from.getUsername());
        setRole(from.getRole());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends IUserRoles> E into(E into) {
        into.from(this);
        return into;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UserRolesRecord
     */
    public UserRolesRecord() {
        super(UserRoles.USER_ROLES);
    }

    /**
     * Create a detached, initialised UserRolesRecord
     */
    public UserRolesRecord(String username, String role) {
        super(UserRoles.USER_ROLES);

        set(0, username);
        set(1, role);
    }
}
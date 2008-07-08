package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.UserApi;

import java.util.Random;

public class UserTestBase extends HQApiTestBase {

    static final String PASSWORD = "apitest";

    static final String TESTUSER_NAME_PREFIX = "apitest";
    static final String TESTUSER_FIRSTNAME   = "API";
    static final String TESTUSER_LASTNAME    = "Test";
    static final String TESTUSER_EMAIL       = "apitest@hyperic.com";
    static final boolean TESTUSER_ACTIVE     = true;

    public UserTestBase(String name) {
        super(name);
    }

    UserApi getUserApi() {
        return getApi().getUserApi();
    }

    UserApi getUserApi(String user, String password) {
        return getApi(user, password).getUserApi();
    }

    /**
     * Generate a valid User object that's guaranteed to have a unique Name
     * @return A valid User object.
     */
    public User generateTestUser() {

        Random r = new Random();

        User user = new User();
        user.setName(TESTUSER_NAME_PREFIX + r.nextInt());
        user.setFirstName(TESTUSER_FIRSTNAME);
        user.setLastName(TESTUSER_LASTNAME);
        user.setEmailAddress(TESTUSER_EMAIL);
        user.setActive(TESTUSER_ACTIVE);
        return user;
    }
}
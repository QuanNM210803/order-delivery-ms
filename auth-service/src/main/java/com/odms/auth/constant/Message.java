package com.odms.auth.constant;

public class Message {
    /*
     * Error messages
     * */
    public static final String USERNAME_NOT_EXISTS = "username.not.exists";
    public static final String USERNAME_EXISTS = "username.exists";
    public static final String LOGIN_FAILED= "login.failed";
    public static final String USER_NOT_VERIFIED= "user.not.verified";
    public static final String USER_ALREADY_VERIFIED="user.already.verified";
    public static final String UPDATE_STATUS_FINDING_ORDER_FAILED="update.status.finding.order.failed";
    public static final String EMAIL_EXISTS="email.exists";

    /*
     * Validation messages
     * */
    public static final String LOGIN_USERNAME_REQUIRE="login.username.require";
    public static final String LOGIN_PASSWORD_REQUIRE="login.password.require";
    public static final String REGISTER_USERNAME_REQUIRE="register.username.require";
    public static final String REGISTER_PASSWORD_REQUIRE="register.password.require";
    public static final String REGISTER_FULL_NAME_REQUIRE="register.full.name.require";
    public static final String REGISTER_PHONE_REQUIRE="register.phone.require";
    public static final String REGISTER_EMAIL_REQUIRE="register.email.require";
    public static final String REGISTER_USERNAME_INVALID="register.username.invalid";
    public static final String REGISTER_PASSWORD_INVALID="register.password.invalid";
    public static final String REGISTER_PHONE_INVALID="register.phone.invalid";
    public static final String REGISTER_EMAIL_INVALID="register.email.invalid";

    /*
     * Other messages
     * */
    public static final String LOGIN_SUCCESS = "login.success";
    public static final String REGISTER_SUCCESS="register.success";
    public static final String VERIFY_EMAIL_SUCCESS="verify.email.success";
    public static final String UPDATE_STATUS_FINDING_ORDER_SUCCESS = "update.status.finding.order.success";

}

package com.enterprise.iam.constants;

public class AppConstants {

    private AppConstants() {
    }

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    public static final String TOKEN_TYPE = "Bearer";

    public static final String LOGIN_SUCCESS = "Login Successful";
    public static final String LOGOUT_SUCCESS = "Logout Successful";
    public static final String REGISTER_SUCCESS = "User Registered Successfully";

    public static final String USER_NOT_FOUND = "User Not Found";
    public static final String ROLE_NOT_FOUND = "Role Not Found";
    public static final String PERMISSION_NOT_FOUND = "Permission Not Found";

    public static final String INVALID_CREDENTIALS = "Invalid Username or Password";
    public static final String OTP_SENT = "OTP Sent Successfully";
    public static final String OTP_VERIFIED = "OTP Verified Successfully";
    public static final String PASSWORD_RESET = "Password Reset Successfully";

}
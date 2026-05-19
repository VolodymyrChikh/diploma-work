package com.volodymyrchikh.abitandstudhelp.common;

public final class AppConstants {

    public static final String EMAIL_REGEX =
            "[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";

    public static final String PASSWORD_REGEX =
            "(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])\\S{8,}";

}

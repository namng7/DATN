package com.namng7.datn_v1.cache;

import jakarta.servlet.http.PushBuilder;

public class Key {
    public static class Status {
        public static final int ACTIVE = 1;
        public static final int INACTIVE = 0;
    }

    public static class Role {
        public static final int ADMIN = 1;
        public static final int BUSSINESS = 2;
        public static final int COMPANY = 3;
        public static final int CUSTOMER = 4;
    }

    public static class ErrorCode {
        public static final int SUCCESS = 0;
        public static final int SYSTEM_FAULT = 1;
        public static final int NOT_CONDITION = 2;

        // <editor-fold desc="ErrorCode tac dong user">
        public static final int INVALID_USER = 11;
        public static final int INVALID_COMPANY_USER = 12;
        public static final int NOT_AUTH_CHANGE_INFO = 13;
        // </editor-fold>

        // <editor-fold desc="ErrorCode tac dong company">
        public static final int INVALID_COMPANY = 21;
        // </editor-fold>

        // <editor-fold desc="ErrorCode tac dong package">
        public static final int INVALID_PACKAGE = 31;
        // </editor-fold>

        // <editor-fold desc="ErrorCode tac dong service config">
        public static final int INVALID_SERVICE = 41;
        // </editor-fold>

        // <editor-fold desc="ErrorCode tac dong gamecode model">
        public static final int INVALID_MODEL = 51;
        // </editor-fold>

        // <editor-fold desc="ErrorCode tac dong company">

        // </editor-fold>
    }

    public static class Message {
        // <editor-fold desc="Message tac dong user">
        public static final String NULL_MESSAGE = "Chưa cấu hình thông điệp";
        public static final String SYSTEM_FAULT = "SYSTEM_FAULT";
        public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
        public static final String INVALID_USER = "INVALID_USER";
        public static final String INVALID_REGISTER_USER = "INVALID_REGISTER_USER";
        public static final String INVALID_COMPANY_USER = "INVALID_COMPANY_USER";
        public static final String NOT_AUTH_CHANGE_INFO = "NOT_AUTH_CHANGE_INFO";
        public static final String REGISTER_USER_SUCCESS = "REGISTER_USER_SUCCESS";
        public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
        public static final String UPDATE_USER_INFO_SUCCESS = "UPDATE_USER_INFO_SUCCESS";
        public static final String GET_USER_INFO_SUCCESS = "GET_USER_INFO_SUCCESS";
        // </editor-fold>

        // <editor-fold desc="Message tac dong company">
        public static final String INVALID_REGISTER_COMPANY = "INVALID_REGISTER_COMPANY";
        public static final String REGISTER_COMPANY_SUCCESS = "REGISTER_COMPANY_SUCCESS";
        public static final String INVALID_COMPANY = "INVALID_COMPANY";
        public static final String NOT_CONDITION_UPDATE_COMPANY = "NOT_CONDITION_UPDATE_COMPANY";
        // </editor-fold>

        // <editor-fold desc="Message tac dong package">
        public static final String INVALID_ADD_PACKAGE = "INVALID_ADD_PACKAGE";
        public static final String ADD_PACKAGE_SUCCESS = "ADD_PACKAGE_SUCCESS";
        public static final String UPDATE_PACKAGE_SUCCESS = "UPDATE_PACKAGE_SUCCESS";
        public static final String GET_PACKAGE_SUCCESS = "GET_PACKAGE_SUCCESS";
        // </editor-fold>

        // <editor-fold desc="ErrorCode tac dong service config">
        public static final String INVALID_ADD_SERVICE = "INVALID_ADD_SERVICE";
        public static final String INVALID_GET_SERVICE = "INVALID_GET_SERVICE";
        public static final String ADD_SERVICE_SUCCESS = "ADD_SERVICE_SUCCESS";
        public static final String UPDATE_SERVICE_SUCCESS = "UPDATE_SERVICE_SUCCESS";
        public static final String GET_SERVICE_BY_COMPANY_SUCCESS = "GET_SERVICE_BY_COMPANY_SUCCESS";
        public static final String GET_SERVICE_BY_COMPANY_FAULT = "GET_SERVICE_BY_COMPANY_FAULT";
        // </editor-fold>

        // <editor-fold desc="ErrorCode tac dong gamecodeModel">
        public static final String INVALID_ADD_MODEL = "INVALID_ADD_MODEL";
        public static final String ADD_MODEL_SUCCESS = "ADD_MODEL_SUCCESS";
        public static final String UPDATE_MODEL_SUCCESS = "UPDATE_MODEL_SUCCESS";

        public static final String GET_MODEL_SUCCESS = "GET_MODEL_SUCCESS";
        // </editor-fold>

        // <editor-fold desc="ErrorCode tac dong transaction">
        public static final String INVALID_TRANS_BUY_GAMECODE = "INVALID_TRANS_BUY_GAMECODE";
        public static final String INVALID_TRANS_TOP_UP = "INVALID_TRANS_TOP_UP";
        public static final String NOT_ENOUGH_MONEY = "NOT_ENOUGH_MONEY";
        public static final String BUY_GAMECODE_SUCCESS = "BUY_GAMECODE_SUCCESS";
        public static final String CREATE_TRANS_TOPUP_SUCCESS = "CREATE_TRANS_TOPUP_SUCCESS";
        public static final String ACCEPT_TRANS_TOPUP_SUCCESS = "ACCEPT_TRANS_TOPUP_SUCCESS";
        public static final String GET_TRANS_BUY_GAMECODE_SUCCESS = "GET_TRANS_BUY_GAMECODE_SUCCESS";
        public static final String GET_TRANS_TOP_UP_SUCCESS = "GET_TRANS_TOP_UP_SUCCESS";
        // </editor-fold>

        // <editor-fold desc="ErrorCode tac dong company">

        // </editor-fold>
    }

}

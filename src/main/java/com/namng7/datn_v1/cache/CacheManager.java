package com.namng7.datn_v1.cache;

import com.namng7.datn_v1.model.*;

import java.util.List;
import java.util.Map;

public class CacheManager {

    public static Map<String, String> MapMessageByMessageCode;
    public static Map<Long, GamecodeModel> MapGamecodeModelByID;
    public static List<GamecodeModel> ListAllGamecodeModel;
    public static Map<Long, PackageConfig> MapPackageConfigByID;
    public static List<PackageConfig> ListAllPackageConfig;
    public static Map<Long, WebserviceConfig> MapWsConfigByID;

    public static class Users{
        public static Map<Long, User> MapUserByUserID;
        public static Map<String, User> MapUserByUsername;
        public static User AUTH_USER;
        public static List<User> ListAllUser;
    }

    public static class Companys{
        public static List<Company> ListAllCompany;
        public static Map<Long, Company> MapCompany;
        public static Map<Long, Company> MapCompanyById;

    }

}
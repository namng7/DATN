package com.namng7.datn_v1.service;

import com.namng7.datn_v1.model.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;
import java.util.List;

public interface DataLoaderService extends UserDetailsService {
    List<User> loadAllUser();
    List<Company> loadAllCompany();
    List<GamecodeModel> loadAllGamecodeModel();
    List<PackageConfig> loadAllPackageConfig();
    Map<Long, WebserviceConfig> loadAllWebserviceConfig();
    Map<String, String> loadAllConfiguration();

}

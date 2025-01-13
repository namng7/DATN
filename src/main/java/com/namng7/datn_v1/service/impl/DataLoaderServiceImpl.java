package com.namng7.datn_v1.service.impl;

import com.namng7.datn_v1.model.*;
import com.namng7.datn_v1.repository.*;
import com.namng7.datn_v1.service.CompanyService;
import com.namng7.datn_v1.service.DataLoaderService;
import com.namng7.datn_v1.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataLoaderServiceImpl implements DataLoaderService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private static StringBuilder log = new StringBuilder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private GamecodeModelRepsitory gamecodeModelRepsitory;

    @Autowired
    private PackageConfigRepository packageConfigRepository;

    @Autowired
    private WebserviceConfigRepository webserviceConfigRepository;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Override
    public List<User> loadAllUser() {
        List<User> listUsers = userRepository.findAll();
        if(listUsers == null || listUsers.isEmpty()){
            logger.error("Load user fail! ");
            return new ArrayList<>();
        }
        return listUsers;
    }

    @Override
    public List<Company> loadAllCompany() {
        List<Company> listAllCompany = companyRepository.findAll();
        if(listAllCompany == null || listAllCompany.isEmpty()){
            logger.warn("Load company fail!");
            return new ArrayList<>();
        }
        return listAllCompany;
    }

    @Override
    public List<GamecodeModel> loadAllGamecodeModel() {
        List<GamecodeModel> listAllGamecodeModel = gamecodeModelRepsitory.findAll();
        if(listAllGamecodeModel == null || listAllGamecodeModel.isEmpty()) {
            logger.warn("Load gamecodeModel fail!");
            return listAllGamecodeModel;
        }
        return listAllGamecodeModel;
    }

    @Override
    public List<PackageConfig> loadAllPackageConfig() {
        List<PackageConfig> listAllPackageConfig = packageConfigRepository.findAll();
        if(listAllPackageConfig == null || listAllPackageConfig.isEmpty()) {
            logger.warn("Load packageConfig fail!");
            return new ArrayList<>();
        }
        return listAllPackageConfig;
    }

    @Override
    public Map<Long, WebserviceConfig> loadAllWebserviceConfig() {
        List<WebserviceConfig> listAllWebserviceConfig = webserviceConfigRepository.findAll();
        Map<Long, WebserviceConfig> mapAllWsConfig = new HashMap<>();
        if(listAllWebserviceConfig == null || listAllWebserviceConfig.isEmpty()) {
            logger.warn("Load webserviceConfig fail!");
            return mapAllWsConfig;
        }
        for(WebserviceConfig webserviceConfig : listAllWebserviceConfig){
            mapAllWsConfig.put(webserviceConfig.getId(), webserviceConfig);
        }
        return mapAllWsConfig;
    }

    @Override
    public Map<String, String> loadAllConfiguration() {
        List<Configuration> listAllConfiguration = configurationRepository.findAll();
        Map<String, String> mapAllConfig = new HashMap<>();
        if(listAllConfiguration == null || listAllConfiguration.isEmpty()){
            logger.warn("Load configuration fail!");
            return mapAllConfig;
        }
        for(Configuration configuration : listAllConfiguration){
            mapAllConfig.put(configuration.getKey(), configuration.getContent());
        }
        return mapAllConfig;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}

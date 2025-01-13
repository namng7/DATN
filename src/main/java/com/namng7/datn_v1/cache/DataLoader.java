package com.namng7.datn_v1.cache;

import com.namng7.datn_v1.model.Company;
import com.namng7.datn_v1.model.GamecodeModel;
import com.namng7.datn_v1.model.PackageConfig;
import com.namng7.datn_v1.model.User;
import com.namng7.datn_v1.service.CompanyService;
import com.namng7.datn_v1.service.DataLoaderService;
import com.namng7.datn_v1.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.namng7.datn_v1.cache.CacheManager.ListAllGamecodeModel;
import static com.namng7.datn_v1.cache.CacheManager.MapGamecodeModelByID;

@Component
public class DataLoader implements ApplicationRunner {

    private static final Logger logger = LogManager.getLogger(DataLoader.class);

    private final DataLoaderService dataLoaderService;

    @Autowired
    public DataLoader(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Start load data...");
        try {
            CacheManager.Users.ListAllUser = dataLoaderService.loadAllUser();
            CacheManager.Users.MapUserByUserID = new HashMap<>();
            CacheManager.Users.MapUserByUsername = new HashMap<>();
            for (User user : CacheManager.Users.ListAllUser) {
                CacheManager.Users.MapUserByUserID.put(user.getId(), user);
                CacheManager.Users.MapUserByUsername.put(user.getUsername(), user);

            }
            CacheManager.Companys.ListAllCompany = dataLoaderService.loadAllCompany();
            CacheManager.Companys.MapCompany = new HashMap<>();
            CacheManager.Companys.MapCompanyById = new HashMap<>();
            for (Company company : CacheManager.Companys.ListAllCompany) {
                CacheManager.Companys.MapCompany.put(company.getUser_id(), company);
                CacheManager.Companys.MapCompanyById.put(company.getId(), company);
            }
            ListAllGamecodeModel = dataLoaderService.loadAllGamecodeModel();
            MapGamecodeModelByID = new HashMap<>();
            for (GamecodeModel gamecodeModel : ListAllGamecodeModel) {
                if (gamecodeModel.getStatus() == Key.Status.ACTIVE) {
                    MapGamecodeModelByID.put(gamecodeModel.getId(), gamecodeModel);
                }
            }
            CacheManager.ListAllPackageConfig = dataLoaderService.loadAllPackageConfig();
            CacheManager.MapPackageConfigByID = new HashMap<>();
            for (PackageConfig packageConfig : CacheManager.ListAllPackageConfig) {
                if (packageConfig.getStatus() == Key.Status.ACTIVE) {
                    CacheManager.MapPackageConfigByID.put(packageConfig.getId(), packageConfig);
                }
            }
            CacheManager.MapWsConfigByID = dataLoaderService.loadAllWebserviceConfig();
            CacheManager.MapMessageByMessageCode = dataLoaderService.loadAllConfiguration();
            logger.info("Load data success! ");
        } catch (Exception e) {
            logger.error("Load data fail.", e);
        }


    }
}

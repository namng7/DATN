package com.namng7.datn_v1.service.impl;

import com.namng7.datn_v1.cache.CacheManager;
import com.namng7.datn_v1.cache.Key;
import com.namng7.datn_v1.model.Company;
import com.namng7.datn_v1.model.PackageConfig;
import com.namng7.datn_v1.model.ServiceConfig;
import com.namng7.datn_v1.model.User;
import com.namng7.datn_v1.object.ProcessRecord;
import com.namng7.datn_v1.repository.PackageConfigRepository;
import com.namng7.datn_v1.repository.ServiceConfigReposiory;
import com.namng7.datn_v1.service.PackageConfigService;
import com.namng7.datn_v1.service.ServiceConfigService;
import com.namng7.datn_v1.util.CompanyUtil;
import com.namng7.datn_v1.util.MessageUtil;
import com.namng7.datn_v1.util.ServiceUtil;
import com.namng7.datn_v1.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class ServiceConfigServiceImpl implements ServiceConfigService {
    private static final Logger logger = LogManager.getLogger(ServiceConfigService.class);
    private static StringBuilder log = new StringBuilder();

    @Autowired
    private ServiceConfigReposiory serviceConfigReposiory;

    private void validateServiceConfig(ProcessRecord record) throws Exception {
        User user = CacheManager.Users.MapUserByUsername.get(record.getUser().getUsername());
        if (user == null || (user.getRole() != Key.Role.ADMIN && user.getRole() != Key.Role.BUSSINESS)) {
            record.setErrorCode(Key.ErrorCode.NOT_AUTH_CHANGE_INFO);
            record.setMessage(MessageUtil.getMessage(Key.Message.NOT_AUTH_CHANGE_INFO, logger));
            log.setLength(0);
            log.append("User: ").append(user == null ? "null" : user.getUsername()).
                    append(": tai khoan khong du quyen thay doi thong tin. Role: ").append(user == null ? "null" : user.getRole());
            logger.warn(log.toString());
            return;
        }
        ServiceConfig serviceConfig = ServiceUtil.convertMaptoPojoServiceConfig((LinkedHashMap<String, Object>) record.getObject());
        if (ServiceUtil.validateServiceConfig(serviceConfig) != Key.ErrorCode.SUCCESS) {
            record.setErrorCode(Key.ErrorCode.INVALID_SERVICE);
            record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_ADD_SERVICE, logger));
            log.setLength(0);
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": thong tin goi cau hinh dich vu khong hop le.");
            logger.warn(log.toString());
            return;
        }
        record.setObject(serviceConfig);
        record.setUser(user);
        record.setErrorCode(Key.ErrorCode.SUCCESS);
    }

    @Override
    public void addServiceConfig(ProcessRecord record) {
        try {
            validateServiceConfig(record);
            if (record.getErrorCode() == Key.ErrorCode.SUCCESS) {
                ServiceConfig serviceConfig = (ServiceConfig) record.getObject();
                serviceConfig.setCreate_date(new Date());
                serviceConfig.setExport_status(0l);
                ServiceConfig savedPackage = serviceConfigReposiory.save(serviceConfig);
                record.setObject(savedPackage);
                record.setErrorCode(Key.ErrorCode.SUCCESS);
                record.setMessage(Key.Message.ADD_SERVICE_SUCCESS);
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": them moi thanh cong cau hinh dich vu");
                logger.info(log.toString());
            }
        } catch (Exception e) {
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi them moi goi dich vu.");
            logger.error(log.toString(), e);
        }
    }

    @Override
    public void updateServiceConfig(ProcessRecord record) {
        try {
            validateServiceConfig(record);
            ServiceConfig serviceConfig = (ServiceConfig) record.getObject();
            List<ServiceConfig> targetServices = serviceConfigReposiory.getServiceConfigById(serviceConfig.getId());
            if(targetServices == null || targetServices.isEmpty()){
                record.setErrorCode(Key.ErrorCode.INVALID_SERVICE);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_ADD_SERVICE, logger));
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": thong tin goi cau hinh dich vu khong hop le.");
                logger.warn(log.toString());
                return;
            }
            ServiceConfig targetService = targetServices.get(0);
            ServiceUtil.mergeServiceConfigInfor(record, targetService);
            ServiceConfig savedService = serviceConfigReposiory.save(targetService);
            record.setObject(savedService);
            record.setErrorCode(Key.ErrorCode.SUCCESS);
            record.setMessage(Key.Message.UPDATE_SERVICE_SUCCESS);
            log.setLength(0);
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": them moi thanh cong cau hinh dich vu");
            logger.info(log.toString());
        } catch (Exception e) {
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi thay doi thong tin goi dich vu.");
            logger.error(log.toString(), e);
        }
    }

    @Override
    public void getServiceConfigByCompanyId(ProcessRecord record){
        List<ServiceConfig> listServiceConfig = new ArrayList<>();
        try{
            UserUtil.validateUserRecord(record, log, logger);
            if(record.getErrorCode() != Key.ErrorCode.SUCCESS){
                return;
            }
            User user = record.getUser();
            Company companyInfo = CompanyUtil.convertMaptoPojo((LinkedHashMap<String, Object>) record.getObject());
            Company company = CacheManager.Companys.MapCompany.get(companyInfo.getUser_id());
            if(company == null){
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_REGISTER_COMPANY, logger));
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": thong tin doanh nghiep khong ton tai.");
                logger.warn(log.toString());
                return;
            }
            if(user.getRole() == Key.Role.ADMIN ||
                    (user.getRole() == Key.Role.BUSSINESS && company.getBussiness_care().equals(user.getId())) ||
                    (user.getRole() == Key.Role.COMPANY && company.getUser_id().equals(user.getId()))){
                listServiceConfig = serviceConfigReposiory.getServiceConfigByCompanyId(company.getId());
                record.setObject(listServiceConfig);
                record.setUser(user);
                record.setErrorCode(Key.ErrorCode.SUCCESS);
                record.setMessage(Key.Message.GET_SERVICE_BY_COMPANY_SUCCESS);
                log.setLength(0);
                log.append("User: ").append(user.getUsername()).
                        append(": lay thong tin cau hinh dich vu thanh cong cho company: ").append(company.getName());
                logger.info(log.toString());
            }else{
                record.setErrorCode(Key.ErrorCode.NOT_AUTH_CHANGE_INFO);
                record.setMessage(MessageUtil.getMessage(Key.Message.NOT_AUTH_CHANGE_INFO, logger));
                log.setLength(0);
                log.append("User: ").append(user == null ? "null" : user.getUsername()).
                        append(": tai khoan khong du quyen view thong tin. Role: ").append(user == null ? "null" : user.getRole());
                logger.warn(log.toString());
                return;
            }

        } catch (Exception e) {
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi view thong tin goi dich vu.");
            logger.error(log.toString(), e);
        }

    }
}

package com.namng7.datn_v1.service.impl;

import com.namng7.datn_v1.cache.CacheManager;
import com.namng7.datn_v1.cache.Key;
import com.namng7.datn_v1.model.PackageConfig;
import com.namng7.datn_v1.model.User;
import com.namng7.datn_v1.object.ProcessRecord;
import com.namng7.datn_v1.repository.PackageConfigRepository;
import com.namng7.datn_v1.service.PackageConfigService;
import com.namng7.datn_v1.util.MessageUtil;
import com.namng7.datn_v1.util.ServiceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class PackageConfigServiceImpl implements PackageConfigService {
    private static final Logger logger = LogManager.getLogger(PackageConfigService.class);
    private static StringBuilder log = new StringBuilder();

    @Autowired
    private PackageConfigRepository packageConfigRepository;

    private void validateProcess(ProcessRecord record) throws Exception {
        User userInfo = record.getUser();
        User user = CacheManager.Users.MapUserByUsername.get(userInfo.getUsername());
        if (user == null || (user.getRole() != Key.Role.ADMIN && user.getRole() != Key.Role.BUSSINESS)) {
            record.setErrorCode(Key.ErrorCode.NOT_AUTH_CHANGE_INFO);
            record.setMessage(MessageUtil.getMessage(Key.Message.NOT_AUTH_CHANGE_INFO, logger));
            log.setLength(0);
            log.append("User: ").append(user == null ? "null" : user.getUsername()).
                    append(": tai khoan khong ton tai hoac khong du quyen thay doi thong tin. Role: ").append(user == null ? "null" : user.getRole());
            logger.warn(log.toString());
            return;
        }
        PackageConfig packageConfig = ServiceUtil.convertMaptoPojoPackageConfig((LinkedHashMap<String, Object>) record.getObject());
        if (ServiceUtil.validatePackageConfig(packageConfig) != Key.ErrorCode.SUCCESS) {
            record.setErrorCode(Key.ErrorCode.INVALID_PACKAGE);
            record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_ADD_PACKAGE, logger));
            log.setLength(0);
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": thong tin goi dich vu khong hop le.");
            logger.warn(log.toString());
            return;
        }
        record.setUser(user);
        record.setObject(packageConfig);
        record.setErrorCode(Key.ErrorCode.SUCCESS);
    }

    @Override
    public void addPackageConfig(ProcessRecord record) {
        try {
            validateProcess(record);
            if(record.getErrorCode() == Key.ErrorCode.SUCCESS) {
                PackageConfig packageConfig = (PackageConfig) record.getObject();
                packageConfig.setCreate_date(new Date());
                packageConfig.setCreate_user_id(record.getUser().getId());
                PackageConfig savedPackage = ServiceUtil.savePackage(packageConfig, packageConfigRepository);
                record.setObject(savedPackage);
                record.setErrorCode(Key.ErrorCode.SUCCESS);
                record.setMessage(Key.Message.ADD_PACKAGE_SUCCESS);
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": them moi thanh cong goi dich vu: ").append(savedPackage.getPackage_name());
                logger.info(log.toString());
            }
        }catch (Exception e){
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi them moi goi dich vu.");
            logger.error(log.toString(), e);
        }

    }

    @Override
    public void getAllPackageConfigByRole(ProcessRecord record){
        List<PackageConfig> packageConfigList = new ArrayList<>();
        try{
            User user = CacheManager.Users.MapUserByUsername.get(record.getUser().getUsername());
            if (user == null || (user.getRole() != Key.Role.ADMIN && user.getRole() != Key.Role.BUSSINESS)) {
                record.setErrorCode(Key.ErrorCode.NOT_AUTH_CHANGE_INFO);
                record.setMessage(MessageUtil.getMessage(Key.Message.NOT_AUTH_CHANGE_INFO, logger));
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": tai khoan khong ton tai hoac khong du quyen xem thong tin. Role: ").append(user == null ? "null" : user.getRole());
                logger.warn(log.toString());
                return;
            }
            if(Key.Role.ADMIN == user.getRole()){
                packageConfigList = CacheManager.ListAllPackageConfig;
            }else if (user.getRole() == Key.Role.BUSSINESS){
                for(PackageConfig packageConfig : CacheManager.ListAllPackageConfig){
                    if(packageConfig.getStatus() == Key.Status.ACTIVE){
                        packageConfigList.add(packageConfig);
                    }
                }
            }
            record.setObject(packageConfigList);
            record.setUser(user);
            record.setErrorCode(Key.ErrorCode.SUCCESS);
            record.setMessage(Key.Message.GET_PACKAGE_SUCCESS);
            log.setLength(0);
            log.append("User: ").append(user.getUsername()).
                    append(": lay thong tin cau hinh goi thanh cong: ").append(packageConfigList.size());
            logger.info(log.toString());
        }catch (Exception e){
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi lay thong tin goi dich vu.");
            logger.error(log.toString(), e);
        }
    }

    @Override
    public void updatePackageConfig(ProcessRecord record) {
        try{
            validateProcess(record);
            if(record.getErrorCode() == Key.ErrorCode.SUCCESS) {
                PackageConfig mergePackage = (PackageConfig) record.getObject();
                if(mergePackage.getUpdated_reason() == null || mergePackage.getUpdated_reason().isEmpty()){
                    record.setErrorCode(Key.ErrorCode.INVALID_PACKAGE);
                    record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_ADD_PACKAGE, logger));
                    log.setLength(0);
                    log.append("User: ").append(record.getUser().getUsername()).
                            append(": thong tin goi dich vu khong hop le.");
                    logger.warn(log.toString());
                    return;
                }
                PackageConfig targetPackage = CacheManager.MapPackageConfigByID.get(mergePackage.getId());
                ServiceUtil.mergePackageConfigInfor(record, targetPackage);
                PackageConfig savedPackage = packageConfigRepository.save(targetPackage);
                record.setObject(savedPackage);
                record.setErrorCode(Key.ErrorCode.SUCCESS);
                record.setMessage(Key.Message.UPDATE_PACKAGE_SUCCESS);
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": cap nhat thanh cong goi dich vu: ").append(savedPackage.getPackage_name());
                logger.info(log.toString());
            }
        }catch (Exception e){
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi cap nhat goi dich vu.");
            logger.error(log.toString(), e);
        }
    }
}

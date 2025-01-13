package com.namng7.datn_v1.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namng7.datn_v1.cache.CacheManager;
import com.namng7.datn_v1.cache.Key;
import com.namng7.datn_v1.config.CustomObjectMapper;
import com.namng7.datn_v1.model.Company;
import com.namng7.datn_v1.model.GamecodeModel;
import com.namng7.datn_v1.model.PackageConfig;
import com.namng7.datn_v1.model.ServiceConfig;
import com.namng7.datn_v1.object.ProcessRecord;
import com.namng7.datn_v1.repository.GamecodeModelRepsitory;
import com.namng7.datn_v1.repository.PackageConfigRepository;
import com.namng7.datn_v1.repository.ServiceConfigReposiory;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ServiceUtil {

    public static int validatePackageConfig(PackageConfig packageConfig){
        return Key.ErrorCode.SUCCESS;
    }

    public static int validateModelConfig(GamecodeModel gamecodeModel){
        return Key.ErrorCode.SUCCESS;
    }

    public static int validateServiceConfig(ServiceConfig serviceConfig){
        return Key.ErrorCode.SUCCESS;
    }

    public static PackageConfig savePackage(PackageConfig packageConfig, PackageConfigRepository packageConfigRepository) throws Exception{
        PackageConfig savedPackage = packageConfigRepository.save(packageConfig);
        if(CacheManager.MapPackageConfigByID == null){
            CacheManager.MapPackageConfigByID = new HashMap<>();
        }
        CacheManager.MapPackageConfigByID.put(savedPackage.getId(), savedPackage);
        return savedPackage;
    }

    public static GamecodeModel saveGamecodeModel(GamecodeModel gamecodeModel, GamecodeModelRepsitory gamecodeModelRepsitory) throws Exception{
        GamecodeModel savedModel = gamecodeModelRepsitory.save(gamecodeModel);
        if(CacheManager.MapGamecodeModelByID == null){
            CacheManager.MapGamecodeModelByID = new HashMap<>();
        }
        CacheManager.MapGamecodeModelByID.put(savedModel.getId(), savedModel);
        return savedModel;
    }

    public static void mergePackageConfigInfor(ProcessRecord record, PackageConfig targetPackage) throws IllegalAccessException {
        PackageConfig mergePackage = (PackageConfig) record.getObject();
        boolean isChange = false;

        Field[] fields = PackageConfig.class.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);

            Object mergeValue = field.get(mergePackage);

            if(mergeValue != null){
                field.set(targetPackage, mergeValue);
                isChange = true;
            }
        }
        if(isChange){
            targetPackage.setUpdated_user_id(record.getUser().getId());
        }
    }

    public static void mergeGamecodeModelInfor(ProcessRecord record, GamecodeModel targetModel) throws IllegalAccessException {
        GamecodeModel mergeModel = (GamecodeModel) record.getObject();
        boolean isChange = false;

        Field[] fields = GamecodeModel.class.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);

            Object mergeValue = field.get(mergeModel);

            if(mergeValue != null){
                field.set(targetModel, mergeValue);
                isChange = true;
            }
        }
        if(isChange){
            targetModel.setUpdated_user_id(record.getUser().getId());
            targetModel.setUpdated_date(new Date());
        }
    }

    public static void mergeServiceConfigInfor(ProcessRecord record, ServiceConfig targetService) throws IllegalAccessException {
        ServiceConfig mergeService = (ServiceConfig) record.getObject();
        boolean isChange = false;

        Field[] fields = ServiceConfig.class.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);

            Object mergeValue = field.get(mergeService);

            if(mergeValue != null){
                field.set(targetService, mergeValue);
                isChange = true;
            }
        }
        if(isChange){
            targetService.setExport_status(0l);
        }
    }

    public static PackageConfig convertMaptoPojoPackageConfig(LinkedHashMap<String, Object> map) throws Exception{
        ObjectMapper objectMapper = CustomObjectMapper.getObjectMapper();
        return objectMapper.convertValue(map, PackageConfig.class);
    }

    public static GamecodeModel convertMaptoPojoGamecodeModel(LinkedHashMap<String, Object> map) throws Exception{
        ObjectMapper objectMapper = CustomObjectMapper.getObjectMapper();
        return objectMapper.convertValue(map, GamecodeModel.class);
    }

    public static ServiceConfig convertMaptoPojoServiceConfig(LinkedHashMap<String, Object> map) throws Exception{
        ObjectMapper objectMapper = CustomObjectMapper.getObjectMapper();
        return objectMapper.convertValue(map, ServiceConfig.class);
    }
}

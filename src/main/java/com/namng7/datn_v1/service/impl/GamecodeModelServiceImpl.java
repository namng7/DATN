package com.namng7.datn_v1.service.impl;

import com.namng7.datn_v1.cache.CacheManager;
import com.namng7.datn_v1.cache.Key;
import com.namng7.datn_v1.model.Company;
import com.namng7.datn_v1.model.GamecodeModel;
import com.namng7.datn_v1.model.ServiceConfig;
import com.namng7.datn_v1.model.User;
import com.namng7.datn_v1.object.ProcessRecord;
import com.namng7.datn_v1.repository.GamecodeModelRepsitory;
import com.namng7.datn_v1.repository.ServiceConfigReposiory;
import com.namng7.datn_v1.service.GamecodeModelService;
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
public class GamecodeModelServiceImpl implements GamecodeModelService {
    private static final Logger logger = LogManager.getLogger(GamecodeModelService.class);
    private static StringBuilder log = new StringBuilder();

    @Autowired
    private GamecodeModelRepsitory gamecodeModelRepsitory;

    @Autowired
    private ServiceConfigReposiory serviceConfigReposiory;

    private void validateProcess(ProcessRecord record) throws Exception {
        User userinfo = record.getUser();
        User user = CacheManager.Users.MapUserByUsername.get(userinfo.getUsername());
        if (user == null || (user.getRole() != Key.Role.ADMIN && user.getRole() != Key.Role.BUSSINESS)) {
            record.setErrorCode(Key.ErrorCode.NOT_AUTH_CHANGE_INFO);
            record.setMessage(MessageUtil.getMessage(Key.Message.NOT_AUTH_CHANGE_INFO, logger));
            log.setLength(0);
            log.append("User: ").append(user == null ? "null" : user.getUsername()).
                    append(": tai khoan khong ton tai hoac du quyen thay doi thong tin. Role: ").append(user == null ? "null" : user.getRole());
            logger.warn(log.toString());
            return;
        }
        GamecodeModel gamecodeModel = ServiceUtil.convertMaptoPojoGamecodeModel((LinkedHashMap<String, Object>)  record.getObject());
        if (ServiceUtil.validateModelConfig(gamecodeModel) != Key.ErrorCode.SUCCESS) {
            record.setErrorCode(Key.ErrorCode.INVALID_MODEL);
            record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_ADD_MODEL, logger));
            log.setLength(0);
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": thong tin dich vu khong hop le.");
            logger.warn(log.toString());
            return;
        }
        record.setUser(user);
        record.setObject(gamecodeModel);
        record.setErrorCode(Key.ErrorCode.SUCCESS);
    }

    @Override
    public void addGamecodeModel(ProcessRecord record) {
        try {
            validateProcess(record);
            if (record.getErrorCode() == Key.ErrorCode.SUCCESS) {
                GamecodeModel gamecodeModel = (GamecodeModel) record.getObject();
                gamecodeModel.setCreate_date(new Date());
                gamecodeModel.setCreate_user_id(record.getUser().getId());
                gamecodeModel.setUpdated_user_id(record.getUser().getId());
                GamecodeModel savedModel = ServiceUtil.saveGamecodeModel(gamecodeModel, gamecodeModelRepsitory);
                record.setObject(savedModel);
                record.setErrorCode(Key.ErrorCode.SUCCESS);
                record.setMessage(Key.Message.ADD_MODEL_SUCCESS);
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": them moi thanh cong dich vu: ").append(savedModel.getModel_name());
                logger.info(log.toString());
            }
        } catch (Exception e) {
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi them moi dich vu.");
            logger.error(log.toString(), e);
        }
    }

    @Override
    public void updateGamecodeModel(ProcessRecord record) {
        try {
            validateProcess(record);
            if (record.getErrorCode() == Key.ErrorCode.SUCCESS) {
                GamecodeModel mergeModel = (GamecodeModel) record.getObject();
                GamecodeModel targetModel = CacheManager.MapGamecodeModelByID.get(mergeModel.getId());
                if (targetModel == null) {
                    record.setErrorCode(Key.ErrorCode.INVALID_MODEL);
                    record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_ADD_MODEL, logger));
                    log.setLength(0);
                    log.append("User: ").append(record.getUser().getUsername()).
                            append(": thong tin dich vu khong hop le.");
                    logger.warn(log.toString());
                    return;
                }
                ServiceUtil.mergeGamecodeModelInfor(record, targetModel);
                GamecodeModel savedModel = gamecodeModelRepsitory.save(targetModel);
                record.setObject(savedModel);
                record.setErrorCode(Key.ErrorCode.SUCCESS);
                record.setMessage(Key.Message.UPDATE_MODEL_SUCCESS);
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": cap nhat thanh cong dich vu: ").append(savedModel.getModel_name());
                logger.info(log.toString());
            }
        } catch (Exception e) {
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi cap nhat dich vu.");
            logger.error(log.toString(), e);
        }
    }

    @Override
    public void getAllGameCodeModel(ProcessRecord record) {
        try {
            record.setObject(CacheManager.ListAllGamecodeModel);
            record.setErrorCode(Key.ErrorCode.SUCCESS);
            record.setMessage(Key.Message.GET_MODEL_SUCCESS);
            log.setLength(0);
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": view thong tin dich vu thanh cong: ");
            logger.info(log.toString());
        } catch (Exception e) {
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi lay thong tin dich vu.");
            logger.error(log.toString(), e);
        }

    }

    @Override
    public void getAllGameCodeModelByRole(ProcessRecord record) {
        try {
            UserUtil.validateUserRecord(record, log, logger);
            if(record.getErrorCode() != Key.ErrorCode.SUCCESS){
                return;
            }
            if (record.getUser().getRole().equals(Key.Role.ADMIN) || record.getUser().getRole().equals(Key.Role.BUSSINESS)) {
                this.getAllGameCodeModel(record);
            } else if (record.getUser().getRole().equals(Key.Role.COMPANY)) {
                List<GamecodeModel> gamecodeModelList = new ArrayList<>();
                Company company = CacheManager.Companys.MapCompany.get(record.getUser().getId());
                if (company == null) {
                    record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                    record.setMessage(Key.Message.INVALID_COMPANY);
                    log.setLength(0);
                    log.append("User: ").append(record.getUser().getUsername()).
                            append(": tai khoan khong co thong tin doanh nghiep.");
                    logger.warn(log.toString());
                    return;
                }
                List<ServiceConfig> listAllServiceConfig = serviceConfigReposiory.getServiceConfigByCompanyId(company.getId());
                for (ServiceConfig serviceConfig : listAllServiceConfig) {
                    if(CacheManager.MapGamecodeModelByID.get(serviceConfig.getGamecode_model_id()) != null) {
                        gamecodeModelList.add(CacheManager.MapGamecodeModelByID.get(serviceConfig.getGamecode_model_id()));
                    }
                }
                record.setObject(gamecodeModelList);
                record.setErrorCode(Key.ErrorCode.SUCCESS);
                record.setMessage(Key.Message.UPDATE_MODEL_SUCCESS);
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": view thong tin dich vu thanh cong: ");
                logger.info(log.toString());
            } else {
                record.setErrorCode(Key.ErrorCode.NOT_AUTH_CHANGE_INFO);
                record.setMessage(MessageUtil.getMessage(Key.Message.NOT_AUTH_CHANGE_INFO, logger));
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": tai khoan khong du quyen tac dong thong tin. Role: ").append(record.getUser().getRole());
                logger.warn(log.toString());
            }
        } catch (Exception e) {
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi cap nhat dich vu.");
            logger.error(log.toString(), e);
        }
    }
}

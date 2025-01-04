package com.namng7.datn_v1.service.impl;

import com.namng7.datn_v1.model.User;
import com.namng7.datn_v1.object.ProcessRecord;
import com.namng7.datn_v1.repository.UserRepository;
import com.namng7.datn_v1.service.UserService;
import com.namng7.datn_v1.cache.CacheManager;
import com.namng7.datn_v1.cache.Key;
import com.namng7.datn_v1.util.MessageUtil;
import com.namng7.datn_v1.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private static StringBuilder log = new StringBuilder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ProcessRecord registerUser(ProcessRecord registerProcessRecord) {
        try {

            User user = registerProcessRecord.getUser();
            if (user.getUsername() == null || "".equals(user.getUsername())) {
                registerProcessRecord.setErrorCode(Key.ErrorCode.INVALID_USER);
                registerProcessRecord.setMessage(MessageUtil.getMessage(Key.Message.INVALID_REGISTER_USER, logger));
                return registerProcessRecord;
            }
            User cacheUser = CacheManager.Users.MapUserByUsername.get(user.getUsername());
            User registerInfo = user;
            registerInfo.setStatus(Key.Status.INACTIVE);
            if(cacheUser != null && cacheUser.getRole().equals(Key.Role.ADMIN) && registerProcessRecord.getObject()!=null){
                user = cacheUser;
                registerInfo = UserUtil.convertMaptoPojo((LinkedHashMap<String, Object>) registerProcessRecord.getObject());
                registerInfo.setStatus(Key.Status.ACTIVE);
                cacheUser = CacheManager.Users.MapUserByUsername.get(registerInfo.getUsername());
            }
            if (registerInfo.getRole() == null) {
                registerInfo.setRole(Key.Role.COMPANY);
            }
            if (cacheUser != null) {
                registerProcessRecord.setErrorCode(Key.ErrorCode.INVALID_USER);
                registerProcessRecord.setMessage(MessageUtil.getMessage(Key.Message.USER_ALREADY_EXISTS, logger));
                log.setLength(0);
                log.append("User: ").append(cacheUser.getUsername()).
                        append(": Da ton tai.");
                logger.info(log.toString());
                return registerProcessRecord;
            }
            registerInfo.setCreated_time(new Date());
            User savedUser = UserUtil.saveUser(registerInfo, passwordEncoder, userRepository);
            registerProcessRecord.setUser(user);
            registerProcessRecord.setObject(savedUser);
            registerProcessRecord.setErrorCode(Key.ErrorCode.SUCCESS);
            registerProcessRecord.setMessage(MessageUtil.getMessage(Key.Message.REGISTER_USER_SUCCESS, logger));
            log.setLength(0);
            log.append("User: ").append(savedUser.getUsername()).
                    append(": da dang ky thanh cong!");
            logger.info(log.toString());
        } catch (Exception e) {
            log.setLength(0);
            registerProcessRecord.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            registerProcessRecord.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(registerProcessRecord.getUser().getUsername()).
                    append(": loi khi dang ky tai khoan.");
            logger.error(log.toString(), e);
        }

        return registerProcessRecord;
    }

    @Override
    public ProcessRecord loginUser(ProcessRecord loginRecord) {
        User user = CacheManager.Users.MapUserByUsername.get(loginRecord.getUser().getUsername());
        if (user != null && passwordEncoder.matches(loginRecord.getUser().getPassword(), user.getPassword())) {
            loginRecord.setUser(user);
            loginRecord.setErrorCode(Key.ErrorCode.SUCCESS);
            loginRecord.setMessage(MessageUtil.getMessage(Key.Message.LOGIN_SUCCESS, logger));
            log.setLength(0);
            log.append("User: ").append(loginRecord.getUser().getUsername()).
                    append(": da dang nhap thanh cong!");
            logger.info(log.toString());
            return loginRecord;
        } else {
            loginRecord.setErrorCode(Key.ErrorCode.INVALID_USER);
            loginRecord.setMessage(MessageUtil.getMessage(Key.Message.INVALID_USER, logger));
            log.setLength(0);
            log.append("User: ").append(loginRecord.getUser().getUsername()).
                    append(": tai khoan hoac mat khau khong hop le.");
            logger.warn(log.toString());
            return loginRecord;
        }
    }

    @Override
    public ProcessRecord updateInfor(ProcessRecord updateRecord) {
        try {
            User updateInfo = UserUtil.convertMaptoPojo((LinkedHashMap<String, Object>) updateRecord.getObject());
            User user = CacheManager.Users.MapUserByUsername.get(updateRecord.getUser().getUsername());

            if (user == null || updateInfo == null || updateInfo.getUsername() == null
                    || CacheManager.Users.MapUserByUsername.get(updateInfo.getUsername()) == null) {
                updateRecord.setErrorCode(Key.ErrorCode.INVALID_USER);
                updateRecord.setMessage(MessageUtil.getMessage(Key.Message.INVALID_USER, logger));
                log.setLength(0);
                log.append("User: ").append(updateRecord.getUser().getUsername()).
                        append(": tai khoan khong ton tai.");
                logger.warn(log.toString());
                return updateRecord;
            }

            User userUpdate = CacheManager.Users.MapUserByUsername.get(updateInfo.getUsername());

            boolean isPassCheckUserUpdate = false;

            if (userUpdate.getUsername().equals(user.getUsername())) {
                isPassCheckUserUpdate = true;
                log.setLength(0);
                log.append("User: ").append(userUpdate.getUsername()).
                        append(": cap nhat thong tin.");
                logger.info(log.toString());
            } else if (user.getRole() == Key.Role.ADMIN ||
                    (user.getRole() == Key.Role.BUSSINESS && user.getRole() == Key.Role.COMPANY &&
                            (user.getStatus() == 0 || CacheManager.Companys.mapCompany.get(user.getId()).getBussiness_care().equals(CacheManager.Users.AUTH_USER.getId())))) {
                isPassCheckUserUpdate = true;
                log.setLength(0);
                log.append("User: ").append(user.getUsername()).
                        append(": cap nhat thong tin cho user: ").append(userUpdate.getUsername());
                logger.info(log.toString());
            }

            if (isPassCheckUserUpdate) {
                try {
                    UserUtil.mergeInfor(updateInfo, user);
                    User savedUser = UserUtil.saveUser(user, passwordEncoder, userRepository);
                    updateRecord.setUser(savedUser);
                    updateRecord.setErrorCode(Key.ErrorCode.SUCCESS);
                    updateRecord.setMessage(MessageUtil.getMessage(Key.Message.UPDATE_USER_INFO_SUCCESS, logger));
                    log.setLength(0);
                    log.append("User: ").append(user.getUsername()).
                            append(": da cap nhat thong tin thanh cong!");
                    logger.info(log.toString());
                } catch (Exception e) {
                    log.setLength(0);
                    updateRecord.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
                    updateRecord.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
                    log.append("User: ").append(user.getUsername()).
                            append(": loi khi cap nhat thong tin tai khoan");
                    logger.error(log.toString(), e);
                }
            } else {
                updateRecord.setErrorCode(Key.ErrorCode.NOT_AUTH_CHANGE_INFO);
                updateRecord.setMessage(MessageUtil.getMessage(Key.Message.NOT_AUTH_CHANGE_INFO, logger));
                log.setLength(0);
                log.append("User: ").append(updateRecord.getUser().getUsername()).
                        append(": tai khoan khong du quyen thay doi thong tin.");
                logger.warn(log.toString());
                return updateRecord;
            }

        }catch (Exception e){
            log.setLength(0);
            updateRecord.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            updateRecord.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(updateRecord.getUser().getUsername()).
                    append(": loi khi cap nhat thong tin tai khoan.");
            logger.error(log.toString(), e);
        }
        return updateRecord;
    }

    @Override
    public void getUserByUserName(ProcessRecord record) {
        try {
            User userGet = UserUtil.convertMaptoPojo((LinkedHashMap<String, Object>) record.getObject());
            User user = CacheManager.Users.MapUserByUsername.get(record.getUser().getUsername());
            if (user == null || userGet == null || userGet.getUsername() == null
                    || CacheManager.Users.MapUserByUsername.get(userGet.getUsername()) == null) {
                record.setErrorCode(Key.ErrorCode.INVALID_USER);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_USER, logger));
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": tai khoan khong ton tai.");
                logger.warn(log.toString());
                return;
            }
            if (user.getRole() != Key.Role.ADMIN && user.getRole() != Key.Role.BUSSINESS) {
                record.setErrorCode(Key.ErrorCode.NOT_AUTH_CHANGE_INFO);
                record.setMessage(MessageUtil.getMessage(Key.Message.NOT_AUTH_CHANGE_INFO, logger));
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": tai khoan khong du quyen thay doi thong tin.");
                logger.warn(log.toString());
                return;
            }
            record.setUser(userGet);
            record.setErrorCode(Key.ErrorCode.SUCCESS);
            record.setMessage(MessageUtil.getMessage(Key.Message.GET_USER_INFO_SUCCESS, logger));
            log.setLength(0);
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": lay thong tin tai khoan thanh cong.");
            logger.info(log.toString());
        }catch(Exception e){
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi lay thong tin tai khoan.");
            logger.error(log.toString(), e);
        }
    }

}

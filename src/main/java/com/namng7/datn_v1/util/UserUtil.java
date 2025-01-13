package com.namng7.datn_v1.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namng7.datn_v1.cache.CacheManager;
import com.namng7.datn_v1.cache.Key;
import com.namng7.datn_v1.config.CustomObjectMapper;
import com.namng7.datn_v1.model.User;
import com.namng7.datn_v1.object.ProcessRecord;
import com.namng7.datn_v1.repository.UserRepository;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.LinkedHashMap;

public class UserUtil {
    public static int SUCCESS = 0;

    public static void validateUserRecord(ProcessRecord record, StringBuilder log, Logger logger){
        User user = CacheManager.Users.MapUserByUsername.get(record.getUser().getUsername());
        if (user == null) {
            record.setErrorCode(Key.ErrorCode.INVALID_USER);
            record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_USER, logger));
            log.setLength(0);
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": tai khoan khong ton tai.");
            logger.warn(log.toString());
            return;
        }
        record.setErrorCode(Key.ErrorCode.SUCCESS);
        record.setUser(user);
    }

    public static int validateUser(User user){

        return Key.ErrorCode.SUCCESS;
    }

    public static void mergeInfor(PasswordEncoder passwordEncoder, User userFrom, User userTo){
        boolean isChange = false;
        if(userFrom.getPassword() != null && !userFrom.getPassword().isEmpty()){
            userTo.setPassword(passwordEncoder.encode(userFrom.getPassword()));
            isChange = true;
        }
        if(userFrom.getEmail() != null && !userFrom.getEmail().isEmpty()){
            userTo.setEmail(userFrom.getEmail());
            isChange = true;
        }
        if(userFrom.getFullname() != null && !userFrom.getFullname().isEmpty()){
            userTo.setFullname(userFrom.getFullname());
            isChange = true;
        }
        if(userFrom.getStatus() != null){
            userTo.setStatus(userFrom.getStatus());
            isChange = true;
        }
        if(isChange){
            userTo.setUpdated_time(new Date());
        }
    }

    public static User saveUser(User user, UserRepository userRepository) throws Exception{
        User savedUser = userRepository.save(user);
        CacheManager.Users.MapUserByUsername.put(savedUser.getUsername(), savedUser);
        CacheManager.Users.MapUserByUserID.put(savedUser.getId(), savedUser);
        return savedUser;
    }

    public static User convertMaptoPojo(LinkedHashMap<String, Object> map) throws Exception{
        ObjectMapper objectMapper = CustomObjectMapper.getObjectMapper();
        return objectMapper.convertValue(map, User.class);
    }

}

package com.namng7.datn_v1.controller;

import com.namng7.datn_v1.model.User;
import com.namng7.datn_v1.object.ProcessRecord;
import com.namng7.datn_v1.service.DataLoaderService;
import com.namng7.datn_v1.service.UserService;
import com.namng7.datn_v1.service.impl.DataLoaderServiceImpl;
import com.namng7.datn_v1.service.impl.UserServiceImpl;
import com.namng7.datn_v1.cache.CacheManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/datn/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private UserService userServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody ProcessRecord record) {
        try {
            userServiceImpl.registerUser(record);
//            logger.info("Start reload data...");
//            try {
//
//                Map<String, User> userMap = dataLoaderService.loadAllUser();
//
//                CacheManager.Users.MapUserByUsername = userMap;
//                logger.info("Reoad data success! " + userMap.size());
//            }catch (Exception e){
//                logger.error("Load data fail.", e);
//            }
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody ProcessRecord loginRecord) {
        try {
            userServiceImpl.loginUser(loginRecord);
            return ResponseEntity.ok(loginRecord);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(loginRecord);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateInfo(@RequestBody ProcessRecord updateRecord){
        try {
            userServiceImpl.updateInfor(updateRecord);
            return ResponseEntity.ok(updateRecord);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(updateRecord);
        }
    }


    @PostMapping("/getUserByUserName")
    public ResponseEntity<?> getUserByUserName(@RequestBody ProcessRecord record){

        try{
            userServiceImpl.getUserByUserName(record);
            return ResponseEntity.ok(record);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/changePassword")


    @DeleteMapping("/{userAdmin}/{userName}")
    public ResponseEntity<?> removeUser(@PathVariable String userAdmin, @PathVariable String userName){
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok("Done");
    }
}
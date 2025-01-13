package com.namng7.datn_v1.controller;

import com.namng7.datn_v1.model.GamecodeModel;
import com.namng7.datn_v1.model.PackageConfig;
import com.namng7.datn_v1.model.ServiceConfig;
import com.namng7.datn_v1.object.ProcessRecord;
import com.namng7.datn_v1.service.GamecodeModelService;
import com.namng7.datn_v1.service.PackageConfigService;
import com.namng7.datn_v1.service.ServiceConfigService;
import com.namng7.datn_v1.service.TransactionBuyGamecodeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/datn/service")
public class ServiceController {
    private static final Logger logger = LogManager.getLogger(ServiceController.class);

    @Autowired
    private GamecodeModelService gamecodeModelService;

    @Autowired
    private PackageConfigService packageConfigService;

    @Autowired
    private ServiceConfigService serviceConfigService;

    // <editor-fold desc="package config">
    @PostMapping("/addPackage")
    public ResponseEntity<?> addPackageConfig(@RequestBody ProcessRecord record) {
        try {
            packageConfigService.addPackageConfig(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/updatePackageConfig")
    public ResponseEntity<?> updatePackageConfig(@RequestBody ProcessRecord record) {
        try {
            packageConfigService.updatePackageConfig(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/getAllPackageConfig")
    public ResponseEntity<?> getAllPackageConfig(@RequestBody ProcessRecord record) {
        try {
            packageConfigService.getAllPackageConfigByRole(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    // </editor-fold>

    // <editor-fold desc="gamecode model">
    @PostMapping("/addModel")
    public ResponseEntity<?> addGamecodeModel(@RequestBody ProcessRecord record) {
        try {
            gamecodeModelService.addGamecodeModel(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/updateGamecodeModel")
    public ResponseEntity<?> updateGamecodeModel(@RequestBody ProcessRecord record) {
        try {
            gamecodeModelService.updateGamecodeModel(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }


    @PostMapping("/getAllGameCodeModelByRole")
    public ResponseEntity<?> getAllGameCodeModelByRole(@RequestBody ProcessRecord record) {
        try {
            gamecodeModelService.getAllGameCodeModelByRole(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/getAllGameCodeModel")
    public ResponseEntity<?> getAllGameCodeModel(@RequestBody ProcessRecord record) {
        try {
            gamecodeModelService.getAllGameCodeModel(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }
    // </editor-fold>

    // <editor-fold desc="service config">
    @PostMapping("/addServiceConfig")
    public ResponseEntity<?> addServiceConfig(@RequestBody ProcessRecord record) {
        try {
            serviceConfigService.addServiceConfig(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/updateServiceConfig")
    public ResponseEntity<?> updateServiceConfig(@RequestBody ProcessRecord record) {
        try {
            serviceConfigService.updateServiceConfig(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/getServiceConfigByCompanyInfo")
    public  ResponseEntity<?> getServiceConfigByCompanyInfo(@RequestBody ProcessRecord record){
        try{
            serviceConfigService.getServiceConfigByCompanyId(record);
            return ResponseEntity.ok(record);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(record);
        }
    }

//    @PostMapping("/getServiceConfigByRole")
//    public ResponseEntity<?> getServiceConfigByRole(@RequestBody ProcessRecord record){
//        try{
//            serviceConfigService.
//        }
//    }
    // </editor-fold>

}

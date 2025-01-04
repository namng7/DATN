package com.namng7.datn_v1.controller;

import com.namng7.datn_v1.cache.CacheManager;
import com.namng7.datn_v1.cache.Key;
import com.namng7.datn_v1.model.Company;
import com.namng7.datn_v1.model.User;
import com.namng7.datn_v1.object.ProcessRecord;
import com.namng7.datn_v1.service.CompanyService;
import com.namng7.datn_v1.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/datn/company")
public class CompanyController {
    StringBuilder log = new StringBuilder();
    private static final Logger logger = LogManager.getLogger(CompanyController.class);

    @Autowired
    private CompanyService companyService;

    @PostMapping("/register")
    public ResponseEntity<?> registerCompany(@RequestBody ProcessRecord record){
        try{
            companyService.acceptRegisterCompany(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/getCompanyByUserID")
    public  ResponseEntity<?> getCompanyByUserID(@RequestBody ProcessRecord record){
        try {
            User user = UserUtil.convertMaptoPojo((LinkedHashMap<String, Object>) record.getObject());
            if (user.getId() == null || user.getId() < 1) {
                record.setErrorCode(Key.ErrorCode.INVALID_USER);
                record.setMessage(Key.Message.INVALID_USER);
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": tai khoan doanh nghiep khong ton tai.");
                logger.warn(log.toString());
                return ResponseEntity.badRequest().body(record);
            }

            User companyUser = CacheManager.Users.MapUserByUserID.get((user.getId()));

            if (companyUser == null || companyUser.getRole() != Key.Role.COMPANY) {
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY_USER);
                record.setMessage(Key.Message.INVALID_COMPANY_USER);
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": khong phai tai khoan doanh nghiep.");
                logger.warn(log.toString());
                return ResponseEntity.badRequest().body(record);
            }

            Company company = CacheManager.Companys.mapCompany.get(user.getId());

            if (company == null) {
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(Key.Message.INVALID_COMPANY);
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": tai khoan khong co thong tin doanh nghiep.");
                logger.warn(log.toString());
                return ResponseEntity.badRequest().body(record);
            }
            record.setUser(companyUser);
            record.setObject(company);
            return ResponseEntity.ok(record);
        }catch (Exception e){
            log.setLength(0);
            log.append("User: ").append(CacheManager.Users.AUTH_USER.getUsername()).
                    append(": loi khi lay thong tin doanh nghiep.");
            logger.warn(log.toString());
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/updateCompany")
    public ResponseEntity<?> updateCompany(@RequestBody ProcessRecord record){
        try{
            companyService.updateCompany(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/getCompanyByRole")
    public ResponseEntity<?> getCompanyByRole(@RequestBody ProcessRecord record){
        companyService.getAllCompanybyRole(record);
        if(record.getErrorCode() == Key.ErrorCode.SUCCESS){
            return ResponseEntity.ok(record);
        } else {
            return ResponseEntity.badRequest().body(record);
        }
    }


}

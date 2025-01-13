package com.namng7.datn_v1.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namng7.datn_v1.cache.CacheManager;
import com.namng7.datn_v1.cache.Key;
import com.namng7.datn_v1.model.Company;
import com.namng7.datn_v1.model.Wallet;
import com.namng7.datn_v1.object.ProcessRecord;
import com.namng7.datn_v1.repository.WalletRepository;
import com.namng7.datn_v1.service.WalletService;
import com.namng7.datn_v1.util.MessageUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class WalletServiceImpl implements WalletService {
    private static final Logger logger = LogManager.getLogger(WalletService.class);
    private static StringBuilder log = new StringBuilder();

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public void updateWallet(ProcessRecord record) {
        try {
            Wallet updateWallet = convertMapToPojo((LinkedHashMap<String, Object>) record.getObject());
            if(updateWallet.getBalance() == null || updateWallet.getBalance() < 1 || updateWallet.getCompany_id() == null || updateWallet.getCompany_id() <1){
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_COMPANY, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": thong tin vi khong hop le.");
                logger.error(log.toString());
                return;
            }
            Company company = CacheManager.Companys.MapCompany.get(updateWallet.getCompany_id());
            if(company == null){
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_COMPANY, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": thong tin vi khong hop le.");
                logger.error(log.toString());
                return;
            }
            if(!record.getUser().getId().equals(company.getBussiness_care()) && record.getUser().getRole() != Key.Role.ADMIN){
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_COMPANY, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": Khong phari nhan vien kinh doanh hop le");
                logger.error(log.toString());
                return;
            }
            Wallet wallet = walletRepository.getWalletByCompany_id(company.getId());
            if(wallet == null || wallet.getStatus() == Key.Status.INACTIVE){
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_COMPANY, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": thong tin vi khong hop le.");
                logger.error(log.toString());
                return;
            }
            Wallet savedWallet = walletRepository.save(updateWallet);
            record.setObject(savedWallet);
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SUCCESS);
            record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_COMPANY, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": them tien vao vi thanh cong.");
            logger.info(log.toString());
            return;
        }catch (Exception e) {
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi lay thong tin dich vu.");
            logger.error(log.toString(), e);
        }
    }

    public Wallet convertMapToPojo(LinkedHashMap<String, Object> map) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(map, Wallet.class);
    }
}

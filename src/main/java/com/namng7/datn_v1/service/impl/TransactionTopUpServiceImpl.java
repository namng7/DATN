package com.namng7.datn_v1.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namng7.datn_v1.cache.CacheManager;
import com.namng7.datn_v1.cache.Key;
import com.namng7.datn_v1.model.Company;
import com.namng7.datn_v1.model.TransactionTopUp;
import com.namng7.datn_v1.model.Wallet;
import com.namng7.datn_v1.object.ProcessRecord;
import com.namng7.datn_v1.object.TransactionTopUpObj;
import com.namng7.datn_v1.repository.TransactionTopUpRepository;
import com.namng7.datn_v1.repository.WalletRepository;
import com.namng7.datn_v1.service.TransactionTopUpService;
import com.namng7.datn_v1.util.MessageUtil;
import com.namng7.datn_v1.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class TransactionTopUpServiceImpl implements TransactionTopUpService {

    private static final Logger logger = LogManager.getLogger(TransactionTopUpService.class);
    private static StringBuilder log = new StringBuilder();

    @Autowired
    private TransactionTopUpRepository transactionTopUpRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public void getTransTopUpByRole(ProcessRecord record) {
        LocalDateTime defaultEndTime = LocalDateTime.now();
        LocalDateTime defaultStartTime = defaultEndTime.minusMonths(6);
        List<TransactionTopUp> listTransTopUp = new ArrayList<>();
        List<TransactionTopUpObj> lisTransTopUpObj = new ArrayList<>();
        try{
            UserUtil.validateUserRecord(record, log, logger);
            if(record.getErrorCode() != Key.ErrorCode.SUCCESS){
                return;
            }
            if(record.getStartTime() == null){
                record.setStartTime(Date.from(defaultStartTime.atZone(ZoneId.systemDefault()).toInstant()));
            }
            if(record.getEndTime() == null){
                record.setEndTime(Date.from(defaultEndTime.atZone(ZoneId.systemDefault()).toInstant()));
            }
            if(record.getUser().getRole() == Key.Role.ADMIN){
                listTransTopUp = transactionTopUpRepository.findAllByTransTime(record.getStartTime(), record.getEndTime());
                Map<Long, Wallet> mapWallet = new HashMap<>();
                for(TransactionTopUp transactionTopUp : listTransTopUp){
                    Wallet wallet = mapWallet.get(transactionTopUp.getWallet_id());
                    if(wallet == null){
                        wallet = walletRepository.getWalletById(transactionTopUp.getWallet_id());
                        mapWallet.put(wallet.getId(), wallet);
                    }
                    Company company = CacheManager.Companys.MapCompanyById.get(wallet.getCompany_id());
                    lisTransTopUpObj.add(convertToObj(transactionTopUp, wallet, company));
                }
            } else if (record.getUser().getRole() == Key.Role.BUSSINESS) {
                for(Company company : CacheManager.Companys.ListAllCompany){
                    if(company.getBussiness_care().equals(record.getUser().getId())){
                        Wallet wallet = walletRepository.getWalletByCompany_id(company.getId());
                        listTransTopUp= transactionTopUpRepository.findAllByTransTime(wallet.getId(), record.getStartTime(), record.getEndTime());
                        lisTransTopUpObj.addAll(convertToListObj(listTransTopUp, wallet, company));
                    }
                }

            } else if (record.getUser().getRole() == Key.Role.COMPANY) {
                Company company = CacheManager.Companys.MapCompany.get(record.getUser().getId());
                Wallet wallet = walletRepository.getWalletByCompany_id(company.getId());
                listTransTopUp = transactionTopUpRepository.findAllByTransTime(wallet.getId(), record.getStartTime(), record.getEndTime());
                lisTransTopUpObj = convertToListObj(listTransTopUp, wallet, company);
            }
            record.setObject(lisTransTopUpObj);
            record.setErrorCode(Key.ErrorCode.SUCCESS);
            record.setMessage(MessageUtil.getMessage(Key.Message.GET_TRANS_TOP_UP_SUCCESS, logger));
            log.setLength(0);
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": lay thong tin giao dich nap tien thanh cong.");
        }catch (Exception e) {
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi lay thong giao dich nap tien.");
            logger.error(log.toString(), e);
        }
    }

    @Override
    public void getInactiveTransTopUp(ProcessRecord record) {
        List<TransactionTopUp> listTransTopUp = new ArrayList<>();
        List<TransactionTopUpObj> lisTransTopUpObj = new ArrayList<>();
        try{
            UserUtil.validateUserRecord(record, log, logger);
            if(record.getErrorCode() != Key.ErrorCode.SUCCESS){
                return;
            }
            if(record.getUser().getRole() == Key.Role.ADMIN){
                listTransTopUp = transactionTopUpRepository.findAllByStatus(Key.Status.INACTIVE);
                Map<Long, Wallet> mapWallet = new HashMap<>();
                for(TransactionTopUp transactionTopUp : listTransTopUp){
                    Wallet wallet = mapWallet.get(transactionTopUp.getWallet_id());
                    if(wallet == null){
                        wallet = walletRepository.getWalletById(transactionTopUp.getWallet_id());
                        mapWallet.put(wallet.getId(), wallet);
                    }
                    Company company = CacheManager.Companys.MapCompanyById.get(wallet.getCompany_id());
                    lisTransTopUpObj.add(convertToObj(transactionTopUp, wallet, company));
                }
            } else if (record.getUser().getRole() == Key.Role.BUSSINESS) {
                for(Company company : CacheManager.Companys.ListAllCompany){
                    if(company.getBussiness_care().equals(record.getUser().getId())){
                        Wallet wallet = walletRepository.getWalletByCompany_id(company.getId());
                        listTransTopUp= transactionTopUpRepository.findAllByStatus(wallet.getId(), Key.Status.INACTIVE);
                        lisTransTopUpObj.addAll(convertToListObj(listTransTopUp, wallet, company));
                    }
                }

            } else if (record.getUser().getRole() == Key.Role.COMPANY) {
                Company company = CacheManager.Companys.MapCompany.get(record.getUser().getId());
                Wallet wallet = walletRepository.getWalletByCompany_id(company.getId());
                listTransTopUp = transactionTopUpRepository.findAllByStatus(wallet.getId(), Key.Status.INACTIVE);
                lisTransTopUpObj = convertToListObj(listTransTopUp, wallet, company);
            }
            record.setObject(lisTransTopUpObj);
            record.setErrorCode(Key.ErrorCode.SUCCESS);
            record.setMessage(MessageUtil.getMessage(Key.Message.GET_TRANS_TOP_UP_SUCCESS, logger));
            log.setLength(0);
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": lay thong tin giao dich nap tien thanh cong.");
        }catch (Exception e) {
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi lay thong giao dich nap tien.");
            logger.error(log.toString(), e);
        }
    }

    @Override
    public void processTopUp(ProcessRecord record) {
        try{
            UserUtil.validateUserRecord(record, log, logger);
            if(record.getErrorCode() != Key.ErrorCode.SUCCESS){
                return;
            }
            Company company = CacheManager.Companys.MapCompany.get(record.getUser().getId());
            if (company == null) {
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_COMPANY, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": khong tim thay thong tin cong ty.");
                logger.error(log.toString());
                return;
            }
            TransactionTopUp transactionTopUp =  convertMapToPojo((LinkedHashMap<String, Object>)record.getObject());
            if(transactionTopUp == null || transactionTopUp.getValue() == null){
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_TRANS_TOP_UP, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": khong co thong tin giao dich.");
                logger.error(log.toString());
                return;
            }

            Wallet wallet = walletRepository.getWalletByCompany_id(company.getId());
            if (wallet == null) {
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.NOT_ENOUGH_MONEY, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": khong tim thay thong tin vi cong ty.");
                logger.error(log.toString());
                return;
            }

            transactionTopUp.setWallet_id(wallet.getId());
            transactionTopUp.setStatus(Key.Status.INACTIVE);
            transactionTopUp.setBussiness_id(company.getBussiness_care());
            transactionTopUp.setTransaction_time(new Date());
            transactionTopUp.setWallet_balance(wallet.getBalance());
            TransactionTopUp savedTrans = transactionTopUpRepository.save(transactionTopUp);

            record.setObject(savedTrans);
            record.setErrorCode(Key.ErrorCode.SUCCESS);
            record.setMessage(MessageUtil.getMessage(Key.Message.CREATE_TRANS_TOPUP_SUCCESS, logger));
            log.setLength(0);
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": Tao giao dich nap tien thanh cong.");
            logger.info(log.toString());
        }catch (Exception e){
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi tao giao dich nap tien.");
            logger.error(log.toString(), e);
        }
    }

    @Override
    public void acceptTopUp(ProcessRecord record) {
        try{
            UserUtil.validateUserRecord(record, log, logger);
            if(record.getErrorCode() != Key.ErrorCode.SUCCESS){
                return;
            }
            TransactionTopUp transactionTopUpInfo =  convertMapToPojo((LinkedHashMap<String, Object>)record.getObject());
            if(transactionTopUpInfo.getId() == null){
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_TRANS_TOP_UP, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": khong co thong tin giao dich.");
                logger.error(log.toString());
                return;
            }
            TransactionTopUp transactionTopUp = transactionTopUpRepository.getTransactionTopUptById(transactionTopUpInfo.getId());
            if(transactionTopUp == null){
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_TRANS_TOP_UP, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": khong co thong tin giao dich.");
                logger.error(log.toString());
                return;
            }
            if(transactionTopUp.getStatus() == Key.Status.ACTIVE){
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_TRANS_TOP_UP, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": Giao dich da duoc thuc hien.");
                logger.error(log.toString());
                return;
            }
            if(record.getUser().getRole() != Key.Role.ADMIN && !Objects.equals(record.getUser().getId(), transactionTopUp.getBussiness_id())){
                record.setErrorCode(Key.ErrorCode.NOT_AUTH_CHANGE_INFO);
                record.setMessage(MessageUtil.getMessage(Key.Message.NOT_AUTH_CHANGE_INFO, logger));
                log.setLength(0);
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": tai khoan khong du quyen thay doi thong tin. Role: ").append(record.getUser().getRole());
                logger.warn(log.toString());
                return;
            }

            Wallet wallet = walletRepository.getWalletById(transactionTopUp.getWallet_id());
            Long walletBalance = wallet.getBalance();
            walletBalance += transactionTopUp.getValue();
            wallet.setBalance(walletBalance);
            wallet.setLast_topup_id(transactionTopUp.getId());
            Wallet savedWallet =  walletRepository.save(wallet);
            transactionTopUp.setStatus(Key.Status.ACTIVE);
            transactionTopUp.setAccept_time(new Date());
            TransactionTopUp savedTrans = transactionTopUpRepository.save(transactionTopUp);

            record.setObject(savedTrans);
            record.setErrorCode(Key.ErrorCode.SUCCESS);
            record.setMessage(MessageUtil.getMessage(Key.Message.ACCEPT_TRANS_TOPUP_SUCCESS, logger));
            log.setLength(0);
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": nap tien vao vi thanh cong.");
            logger.info(log.toString());
        }catch (Exception e){
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi tao giao dich nap tien.");
            logger.error(log.toString(), e);
        }
    }

    @Override
    public void updateTransTopUp(ProcessRecord record) {

    }

    public TransactionTopUp convertMapToPojo(LinkedHashMap<String, Object> map) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(map, TransactionTopUp.class);
    }

    private List<TransactionTopUpObj> convertToListObj(List<TransactionTopUp> listTrans, Wallet wallet, Company company) throws Exception{
        List<TransactionTopUpObj> listTransObj = new ArrayList<>();
        for(TransactionTopUp transactionTopUp : listTrans){
            listTransObj.add(convertToObj(transactionTopUp, wallet, company));
        }
        return listTransObj;
    }

    private TransactionTopUpObj convertToObj(TransactionTopUp transactionTopUp, Wallet wallet, Company company) throws Exception{
        TransactionTopUpObj transactionTopUpObj = new TransactionTopUpObj();
        transactionTopUpObj.setId(transactionTopUp.getId());
        transactionTopUpObj.setCompanyName(company.getName());
        transactionTopUpObj.setBussinessName(CacheManager.Users.MapUserByUserID.get(company.getBussiness_care()).getFullname());
        transactionTopUpObj.setValue(transactionTopUp.getValue());
        transactionTopUpObj.setWalletBefore(transactionTopUp.getWallet_balance());
        transactionTopUpObj.setWalletAfter(wallet.getBalance());
        transactionTopUpObj.setTransTime(transactionTopUp.getTransaction_time());
        transactionTopUpObj.setAcceptTime(transactionTopUp.getAccept_time());
        return transactionTopUpObj;
    }
}

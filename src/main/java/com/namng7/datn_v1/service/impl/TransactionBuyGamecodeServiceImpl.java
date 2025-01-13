package com.namng7.datn_v1.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namng7.datn_v1.cache.CacheManager;
import com.namng7.datn_v1.cache.Key;
import com.namng7.datn_v1.model.*;
import com.namng7.datn_v1.object.GamecodeDetail;
import com.namng7.datn_v1.object.GenGamecodeRecord;
import com.namng7.datn_v1.object.ProcessRecord;
import com.namng7.datn_v1.object.TransactionBuyGamecodeObj;
import com.namng7.datn_v1.repository.TransBuyGamecodeDetailRepository;
import com.namng7.datn_v1.repository.TransactionBuyGamecodeRepository;
import com.namng7.datn_v1.repository.WalletRepository;
import com.namng7.datn_v1.service.AesEncryptionService;
import com.namng7.datn_v1.service.GamecodeModelService;
import com.namng7.datn_v1.service.TransactionBuyGamecodeService;
import com.namng7.datn_v1.service.UserService;
import com.namng7.datn_v1.util.MessageUtil;
import com.namng7.datn_v1.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class TransactionBuyGamecodeServiceImpl implements TransactionBuyGamecodeService {
    private static final Logger logger = LogManager.getLogger(TransactionBuyGamecodeService.class);
    private static StringBuilder log = new StringBuilder();

    @Autowired
    private GamecodeClientService gamecodeClientService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionBuyGamecodeRepository transactionBuyGamecodeRepository;

    @Autowired
    private GamecodeModelService gamecodeModelService;

    @Autowired
    private TransBuyGamecodeDetailRepository transBuyGamecodeDetailRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AesEncryptionService aesEncryptionService;

    @Override
    public void getAllTransactionBuyGamecodeByRole(ProcessRecord record) {
        LocalDateTime defaultEndTime = LocalDateTime.now();
        LocalDateTime defaultStartTime = defaultEndTime.minusMonths(6);

        List<TransactionBuyGamecode> listAllTransaction = new ArrayList<>();
        List<TransactionBuyGamecodeObj> listResult = new ArrayList<>();
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
                listAllTransaction = transactionBuyGamecodeRepository.findAllByTransTime(record.getStartTime(), record.getEndTime());
            } else if (record.getUser().getRole() == Key.Role.BUSSINESS) {
                for(Company company : CacheManager.Companys.ListAllCompany){
                    if(company.getBussiness_care().equals(record.getUser().getId())){
                        List<TransactionBuyGamecode> listTransCompany = transactionBuyGamecodeRepository.findAllByCompanyID(company.getId(), record.getStartTime(), record.getEndTime());
                        if(listTransCompany != null && !listTransCompany.isEmpty()){
                            listAllTransaction.addAll(listTransCompany);
                        }
                    }
                }
            } else if (record.getUser().getRole() == Key.Role.COMPANY) {
                Company company = CacheManager.Companys.MapCompany.get(record.getUser().getId());
                listAllTransaction = transactionBuyGamecodeRepository.findAllByCompanyID(company.getId(), record.getStartTime(), record.getEndTime());
            }
            if(!listAllTransaction.isEmpty()){
                for(TransactionBuyGamecode trans : listAllTransaction){
                    Company company = CacheManager.Companys.MapCompanyById.get(trans.getCompany_id());
                    User companyUser = CacheManager.Users.MapUserByUserID.get(company.getUser_id());
                    GamecodeModel gamecodeModel = CacheManager.MapGamecodeModelByID.get(trans.getModel_id());
                    TransactionBuyGamecodeObj transObj = new TransactionBuyGamecodeObj();
                    transObj.setUsername(companyUser.getUsername());
                    transObj.setCompanyName(company.getName());
                    transObj.setWalletBefore(trans.getWallet_before());
                    transObj.setWalletAfter(trans.getWallet_after());
                    transObj.setWalletConsumption(trans.getWallet_consumption());
                    transObj.setTransTime(trans.getTransaction_time());
                    transObj.setTotalItem(trans.getTotal_item());
                    transObj.setModelName(gamecodeModel.getModel_name());
                    transObj.setModelDescription(gamecodeModel.getDescription());
                    listResult.add(transObj);
                }
            }
            record.setObject(listResult);
            record.setErrorCode(Key.ErrorCode.SUCCESS);
            record.setMessage(MessageUtil.getMessage(Key.Message.GET_TRANS_BUY_GAMECODE_SUCCESS, logger));
            log.setLength(0);
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": lay thong tin giao dich mua gamecode thanh cong.");
            logger.info(log.toString());
        }catch (Exception e) {
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi lay thong tin giao dich mua gamecode.");
            logger.error(log.toString(), e);
        }
    }

    @Override
    public void buyGameCodeService(ProcessRecord record) {
        try {
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
            TransactionBuyGamecode transactionBuyGamecode =  convertMapToPojo((LinkedHashMap<String, Object>)record.getObject());
            if(transactionBuyGamecode == null ||
                    transactionBuyGamecode.getModel_id() == null || transactionBuyGamecode.getModel_id() < 1l ||
                    transactionBuyGamecode.getTotal_item() == null || transactionBuyGamecode.getTotal_item() < 1){
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_TRANS_BUY_GAMECODE, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": khong co thong tin giao dich.");
                logger.error(log.toString());
                return;
            }
            GamecodeModel gamecodeModel = CacheManager.MapGamecodeModelByID.get(transactionBuyGamecode.getModel_id());
            gamecodeModelService.getAllGameCodeModelByRole(record);
            if(record.getErrorCode() != Key.ErrorCode.SUCCESS){
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.GET_SERVICE_BY_COMPANY_FAULT, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": loi khi lay goi dich vu duoc cau hinh cho doanh nghiep.");
                logger.error(log.toString());
                return;
            }
            boolean isPassValidateServiceConfig = false;
            List<GamecodeModel> offerModel = (List<GamecodeModel>) record.getObject();

            for(GamecodeModel model : offerModel){
                if(model.getId().equals(gamecodeModel.getId())){
                    isPassValidateServiceConfig = true;
                }
            }
            if(!isPassValidateServiceConfig){
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_GET_SERVICE, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": doanh nghiep chua duoc cau hinh dich vu.");
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
            PackageConfig packageConfig = CacheManager.MapPackageConfigByID.get(gamecodeModel.getPackage_id());
            long totalPrice = packageConfig.getPrice() * transactionBuyGamecode.getTotal_item() * (100 - gamecodeModel.getDiscount())/100;
            long walletBefore = wallet.getBalance();
            long walletAfter = walletBefore - totalPrice;
            if(walletAfter < 0){
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.INVALID_COMPANY);
                record.setMessage(MessageUtil.getMessage(Key.Message.INVALID_COMPANY, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": khong du tien trong tai khoan.");
                logger.error(log.toString());
                return;
            }
            transactionBuyGamecode.setWallet_before(wallet.getBalance());
            wallet.setBalance(walletBefore - totalPrice);
            walletRepository.save(wallet);
            GenGamecodeRecord genGamecodeRecord = new GenGamecodeRecord();
            genGamecodeRecord.setAmount(transactionBuyGamecode.getTotal_item());
            String apiUrl = CacheManager.MapWsConfigByID.get(packageConfig.getWs_id()).getApi_url();
            GenGamecodeRecord response = gamecodeClientService.sendPostRequest(apiUrl, genGamecodeRecord).block();
            if(response.getErrorCode() != Key.ErrorCode.SUCCESS || response.getListGamecode() == null || response.getListGamecode().size()<1){
                wallet.setBalance(walletBefore);
                walletRepository.save(wallet);
                log.setLength(0);
                record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
                record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
                log.append("User: ").append(record.getUser().getUsername()).
                        append(": loi khi tao game code.");
                logger.error(log.toString());
                return;
            }
            transactionBuyGamecode.setWallet_consumption(totalPrice);
            transactionBuyGamecode.setWallet_after(walletAfter);
            transactionBuyGamecode.setTransaction_time(new Date());
            transactionBuyGamecode.setCompany_id(company.getId());
            TransactionBuyGamecode savedTrans = transactionBuyGamecodeRepository.save(transactionBuyGamecode);
            StringBuilder emailContent = new StringBuilder();
            emailContent.append("Giao dịch mua game code thành công!").
                    append("\nTên gamecode: " + packageConfig.getPackage_name()).
                    append("\nSố lượng gamecode: " + transactionBuyGamecode.getTotal_item()).
                    append("\nChiết khấu: " + gamecodeModel.getDiscount() + "%").
                    append("\nDanh sách mã gamecode " + packageConfig.getPackage_name() + '\n');
            List<TransactionBuyGamecodeDetail> listDetail = new ArrayList<>();
            for(GamecodeDetail detail : response.getListGamecode()){
                TransactionBuyGamecodeDetail transDetail = new TransactionBuyGamecodeDetail();
                transDetail.setSerial(detail.getSerial());
                transDetail.setTrans_id(savedTrans.getId());
                transDetail.setCreate_date(detail.getCreate_date());
                transDetail.setStart_date(detail.getStart_date());
                transDetail.setValid_date(detail.getValid_date());
                transDetail.setStatus(detail.getStatus());
                listDetail.add(transDetail);
                emailContent.append('\n' + aesEncryptionService.decrypt(detail.getGamecode())).append("\t thời hạn: ").append(detail.getValid_date());
            }
            transBuyGamecodeDetailRepository.saveAll(listDetail);
            logger.info(emailContent.toString());
            //emailService.sendEmail(record.getUser().getEmail(),"TB: Mua gamecode thành công!", emailContent.toString());
            record.setObject(savedTrans);
            record.setErrorCode(Key.ErrorCode.SUCCESS);
            record.setMessage(MessageUtil.getMessage(Key.Message.BUY_GAMECODE_SUCCESS, logger));
            log.setLength(0);
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": mua gamecode thanh cong.");
            logger.info(log.toString());
        }catch (Exception e) {
            log.setLength(0);
            record.setErrorCode(Key.ErrorCode.SYSTEM_FAULT);
            record.setMessage(MessageUtil.getMessage(Key.Message.SYSTEM_FAULT, logger));
            log.append("User: ").append(record.getUser().getUsername()).
                    append(": loi khi mua gamecode.");
            logger.error(log.toString(), e);
        }
    }

    @Override
    public void updateTransactionBuyGamecode(ProcessRecord record) {

    }

    public TransactionBuyGamecode convertMapToPojo(LinkedHashMap<String, Object> map) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(map, TransactionBuyGamecode.class);
    }
}

package com.namng7.datn_v1.controller;

import com.namng7.datn_v1.object.ProcessRecord;
import com.namng7.datn_v1.service.TransactionBuyGamecodeService;
import com.namng7.datn_v1.service.TransactionTopUpService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/datn/transaction")
public class TransactionController {
    private static final Logger logger = LogManager.getLogger(TransactionController.class);

    @Autowired
    private TransactionBuyGamecodeService transactionBuyGamecode;

    @Autowired
    private TransactionTopUpService transactionTopUpService;

    @PostMapping("/buyGamecode")
    public ResponseEntity<?> buyGamecode(@RequestBody ProcessRecord record) {
        try {
            transactionBuyGamecode.buyGameCodeService(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/getAllTransactionBuyGamecodeByRole")
    public ResponseEntity<?> getAllTransactionBuyGamecodeByRole(@RequestBody ProcessRecord record) {
        try {
            transactionBuyGamecode.getAllTransactionBuyGamecodeByRole(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/processTopUp")
    public ResponseEntity<?> processTopUp(@RequestBody ProcessRecord record) {
        try {
            transactionTopUpService.processTopUp(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/getTransTopUpByRole")
    public ResponseEntity<?> getTransTopUpByRole(@RequestBody ProcessRecord record) {
        try {
            transactionTopUpService.getTransTopUpByRole(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/getInactiveTransTopUp")
    public ResponseEntity<?> getInactiveTransTopUp(@RequestBody ProcessRecord record) {
        try {
            transactionTopUpService.getInactiveTransTopUp(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

    @PostMapping("/acceptTopUp")
    public ResponseEntity<?> acceptTopUp(@RequestBody ProcessRecord record) {
        try {
            transactionTopUpService.acceptTopUp(record);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(record);
        }
    }

}

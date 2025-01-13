package com.namng7.datn_v1.service;

import com.namng7.datn_v1.object.ProcessRecord;

public interface TransactionBuyGamecodeService {
    void getAllTransactionBuyGamecodeByRole(ProcessRecord record);
    void buyGameCodeService(ProcessRecord record);
    void updateTransactionBuyGamecode(ProcessRecord record);
}

package com.namng7.datn_v1.service;

import com.namng7.datn_v1.object.ProcessRecord;

public interface TransactionTopUpService {
    void getTransTopUpByRole(ProcessRecord record);
    void getInactiveTransTopUp(ProcessRecord record);
    void processTopUp(ProcessRecord record);
    void acceptTopUp(ProcessRecord record);
    void updateTransTopUp(ProcessRecord record);
}

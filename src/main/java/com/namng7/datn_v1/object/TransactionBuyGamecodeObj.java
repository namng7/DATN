package com.namng7.datn_v1.object;

import lombok.Data;

import java.util.Date;

@Data
public class TransactionBuyGamecodeObj {
    String username;
    String companyName;
    Long walletBefore;
    Long walletAfter;
    Long walletConsumption;
    Date transTime;
    Integer totalItem;
    String modelName;
    String modelDescription;
}

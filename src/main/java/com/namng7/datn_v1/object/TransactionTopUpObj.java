package com.namng7.datn_v1.object;

import lombok.Data;

import java.util.Date;

@Data
public class TransactionTopUpObj {
    private Long id;
    private String companyName;
    private String bussinessName;
    private Long value;
    private Long walletBefore;
    private Long walletAfter;
    private Date transTime;
    private Date acceptTime;

}

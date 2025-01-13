package com.namng7.datn_v1.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "transaction_buy_gamecode_detail")
public class TransactionBuyGamecodeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long trans_id;

    @Column(nullable = false)
    private String serial;

    @Column(nullable = false)
    private Date start_date;

    @Column
    private Date valid_date;

    @Column(nullable = false)
    private Date create_date;

    @Column(nullable = false)
    private Integer status;
}

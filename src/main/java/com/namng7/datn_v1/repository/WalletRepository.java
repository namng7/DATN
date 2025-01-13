package com.namng7.datn_v1.repository;

import com.namng7.datn_v1.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query("select w from Wallet w where w.company_id = :company_id")
    Wallet getWalletByCompany_id(@Param("company_id") Long company_id);

    @Query("select w from Wallet w where w.id = :id")
    Wallet getWalletById(@Param("id") Long walletId);
}

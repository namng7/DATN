package com.namng7.datn_v1.repository;

import com.namng7.datn_v1.model.TransactionBuyGamecode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TransactionBuyGamecodeRepository extends JpaRepository<TransactionBuyGamecode, Long> {
    List<TransactionBuyGamecode> findAll();

    @Query("select t from TransactionBuyGamecode t where t.company_id = :company_id and t.transaction_time > :start_time and t.transaction_time < :end_time")
    List<TransactionBuyGamecode> findAllByCompanyID(@Param("company_id") Long companyID, @Param("start_time") Date startTime, @Param("end_time") Date endTime);

    @Query("select t from TransactionBuyGamecode t where t.transaction_time > :start_time and t.transaction_time < :end_time")
    List<TransactionBuyGamecode> findAllByTransTime(@Param("start_time") Date startTime, @Param("end_time") Date endTime);
}

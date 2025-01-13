package com.namng7.datn_v1.repository;

import com.namng7.datn_v1.model.TransactionTopUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TransactionTopUpRepository extends JpaRepository<TransactionTopUp, Long> {
    @Query("select t from TransactionTopUp t where t.id = :id")
    TransactionTopUp getTransactionTopUptById(@Param("id") Long transId);

    @Query("select t from TransactionTopUp t where t.wallet_id = :wallet_id and t.transaction_time > :start_time and t.transaction_time < :end_time")
    List<TransactionTopUp> findAllByTransTime(@Param("wallet_id") Long wallet_id, @Param("start_time") Date startTime, @Param("end_time") Date endTime);

    @Query("select t from TransactionTopUp t where t.transaction_time > :start_time and t.transaction_time < :end_time")
    List<TransactionTopUp> findAllByTransTime(@Param("start_time") Date startTime, @Param("end_time") Date endTime);

    @Query("select t from TransactionTopUp t where t.wallet_id = :wallet_id and t.status = :status")
    List<TransactionTopUp> findAllByStatus(@Param("wallet_id") Long wallet_id, @Param("status") Integer status);

    @Query("select t from TransactionTopUp t where t.status = :status")
    List<TransactionTopUp> findAllByStatus(@Param("status") Integer status);
}

package com.namng7.datn_v1.service;

import com.namng7.datn_v1.model.Company;
import com.namng7.datn_v1.object.ProcessRecord;

import java.util.Map;

public interface CompanyService {
    ProcessRecord getCompanyByUserId(ProcessRecord record);
    void acceptRegisterCompany(ProcessRecord record);
    void updateCompany(ProcessRecord record);
    void getAllCompanybyRole(ProcessRecord record);
}

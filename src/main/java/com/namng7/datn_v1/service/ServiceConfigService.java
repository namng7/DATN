package com.namng7.datn_v1.service;

import com.namng7.datn_v1.object.ProcessRecord;

public interface ServiceConfigService {
    void addServiceConfig(ProcessRecord record);
    void updateServiceConfig(ProcessRecord record);
    void getServiceConfigByCompanyId(ProcessRecord record);
}

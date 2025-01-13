package com.namng7.datn_v1.service;

import com.namng7.datn_v1.object.ProcessRecord;

public interface PackageConfigService {
    void addPackageConfig(ProcessRecord record);
    void getAllPackageConfigByRole(ProcessRecord record);
    void updatePackageConfig(ProcessRecord record);
}

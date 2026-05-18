package org.example.ikichi_staffcard_project.service;

import org.example.ikichi_staffcard_project.dto.UsageLog;
import org.example.ikichi_staffcard_project.dto.UsageResponse;
import org.example.ikichi_staffcard_project.repository.UsageLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsageLogService {

    private final UsageLogRepository usageLogRepository;
    @Autowired
    public UsageLogService(UsageLogRepository usageLogRepository) {
        this.usageLogRepository = usageLogRepository;
    }

    public UsageLog processCouponUsage(Integer userId, Integer storeId) {
        // 1. 本日の利用回数をチェック
        int usageCount = usageLogRepository.getTodayUsageCount(userId);

        // 2. すでに利用済み（1回以上）ならエラーを投げる
        if (usageCount >= 1) {
            throw new RuntimeException("本日は既に使用済みです。クーポンは1日1回まで利用可能です。");
        }

        // 3. 利用ログの作成と保存
        UsageLog log = new UsageLog();
        log.setUserId(userId);
        log.setUsedAtStoreId(storeId);

        usageLogRepository.save(log);
        return log;
    }

    public List<UsageResponse> getAllLogs(){
        return usageLogRepository.findAllLogs();
    }
}

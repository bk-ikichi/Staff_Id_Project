package org.example.ikichi_staffcard_project.service;

import org.example.ikichi_staffcard_project.dto.UsageLog;
import org.example.ikichi_staffcard_project.dto.UsageResponse;
import org.example.ikichi_staffcard_project.repository.UsageLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsageLogService {

    private final UsageLogRepository usageLogRepository;

    @Autowired
    public UsageLogService(UsageLogRepository usageLogRepository) {
        this.usageLogRepository = usageLogRepository;
    }

    /**
     * 指定した店舗のクーポンが今日使用済みかチェックする
     */
    public boolean isCouponUsedToday(Integer userId, Integer storeId) {
        return usageLogRepository.getTodayUsageCountByStore(userId, storeId) > 0;
    }

    /**
     * クーポン利用を処理し、ログを保存する
     */
    @Transactional
    public UsageLog processCouponUsage(Integer userId, Integer storeId) {
        // 1. 対象店舗の本日利用回数をチェック
        if (isCouponUsedToday(userId, storeId)) {
            throw new RuntimeException("本日は既に使用済みです。この店舗のクーポンは1日1回まで利用可能です。");
        }

        // 2. 利用ログの作成と保存
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
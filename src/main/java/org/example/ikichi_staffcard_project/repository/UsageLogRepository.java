package org.example.ikichi_staffcard_project.repository;

import org.example.ikichi_staffcard_project.dto.UsageLog;
import org.example.ikichi_staffcard_project.dto.UsageResponse;
import org.example.ikichi_staffcard_project.mapper.UsageLogMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsageLogRepository {
    private final UsageLogMapper usageLogMapper;

    public UsageLogRepository(UsageLogMapper usageLogMapper) {
        this.usageLogMapper = usageLogMapper;
    }

    public void save(UsageLog log) {
        usageLogMapper.insertLog(log);
    }

    public int getTodayUsageCount(Integer userId) {
        return usageLogMapper.countTodayUsage(userId);
    }

    public List<UsageResponse> findAllLogs(){
        return usageLogMapper.findAllWithNames();
    }
}

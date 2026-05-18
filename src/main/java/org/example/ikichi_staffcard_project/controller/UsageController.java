package org.example.ikichi_staffcard_project.controller;

import org.example.ikichi_staffcard_project.dto.UsageLog;
import org.example.ikichi_staffcard_project.dto.UsageResponse;
import org.example.ikichi_staffcard_project.service.UsageLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usage")
public class UsageController {
    private final UsageLogService usageLogService;

    @Autowired
    public UsageController(UsageLogService usageLogService) {
        this.usageLogService = usageLogService;
    }

    @PostMapping("/use")
    public ResponseEntity<?> useCoupon(@RequestBody UsageLog usageLog) {
        try{
            UsageLog result = usageLogService.processCouponUsage(
                    usageLog.getUserId(),
                    usageLog.getUsedAtStoreId()
            );
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<UsageResponse>> getAllLogs(){
        List<UsageResponse> logs = usageLogService.getAllLogs();
        return ResponseEntity.ok(logs);
    }
}

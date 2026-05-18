package org.example.ikichi_staffcard_project.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.example.ikichi_staffcard_project.dto.UsageLog;
import org.example.ikichi_staffcard_project.dto.UsageResponse;
import org.example.ikichi_staffcard_project.dto.User;

import java.util.List;

@Mapper
public interface UsageLogMapper {

    // ログ一覧を、ユーザー名と店舗名付きで取得
    @Select("SELECT l.*, u.name AS userName, s.com_name AS storeName " +
            "FROM usage_logs l " +
            "JOIN users u ON l.user_id = u.id " +
            "JOIN stores s ON l.used_at_store_id = s.id " +
            "ORDER BY l.used_at DESC")
    List<UsageResponse> findAllWithNames();

    // ログを記録する
    @Insert("INSERT INTO usage_logs (user_id, used_at_store_id) VALUES (#{userId}, #{usedAtStoreId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertLog(UsageLog log);

    // 指定したユーザーが、今日（サーバー日付）すでに何回利用したかカウントする
    @Select("SELECT COUNT(*) FROM usage_logs " +
            "WHERE user_id = #{userId} " +
            "AND used_at >= CURRENT_DATE " +
            "AND used_at < CURRENT_DATE + 1")
    int countTodayUsage(Integer userId);
}

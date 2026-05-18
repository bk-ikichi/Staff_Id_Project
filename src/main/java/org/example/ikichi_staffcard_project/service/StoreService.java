package org.example.ikichi_staffcard_project.service;

import org.example.ikichi_staffcard_project.dto.Store;
import org.example.ikichi_staffcard_project.dto.User;
import org.example.ikichi_staffcard_project.mapper.UserMapper;
import org.example.ikichi_staffcard_project.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {
    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    public Store insert(Store store) {
        storeRepository.insertStore(store);
        return store;
    }

    @Autowired
    private UserMapper userMapper;

    public Store findByStoreWithUsers(Integer id) {
        Store store = storeRepository.getStoreWithUsers(id);
        if (store != null) {
            // 手動でユーザーリストを取得してセットしてみる
            List<User> users = userMapper.findByStoreId(id);
            System.out.println("取得したユーザー数: " + users.size()); // ログに出力
            store.setUsers(users);
        }
        return store;
    }

    public void changeStoreStatus(Integer id, Boolean isActive) {
        storeRepository.updateActiveStatus(id, isActive);
    }
}
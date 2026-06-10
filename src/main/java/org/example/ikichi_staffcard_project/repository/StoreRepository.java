package org.example.ikichi_staffcard_project.repository;

import org.example.ikichi_staffcard_project.dto.Store;
import org.example.ikichi_staffcard_project.mapper.StoreMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StoreRepository {

    private final StoreMapper storeMapper;

    public StoreRepository(StoreMapper storeMapper) {
        this.storeMapper = storeMapper;
    }

    public List<Store> findAll() {
        return storeMapper.findAll();
    }

    public int insertStore(Store storeDTO){
        return storeMapper.insert(storeDTO);
    }

    public Store getStoreWithUsers(Integer id){
        return storeMapper.findByIdWithUsers(id);
    }

    // StoreRepository.java 内に追記してください
    public void updateStoreFields(Integer id, String comId, String comName, String location) {
        storeMapper.updateStoreFields(id, comId, comName, location);
    }
}

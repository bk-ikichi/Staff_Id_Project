package org.example.ikichi_staffcard_project.controller;


import org.example.ikichi_staffcard_project.dto.Store;
import org.example.ikichi_staffcard_project.repository.StoreRepository;
import org.example.ikichi_staffcard_project.service.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/store")
public class StoreController {
    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

//    一覧取得
    @GetMapping
    public ResponseEntity<List<Store>> getAll(){
        List<Store> stores = storeService.findAll();
        return ResponseEntity.ok(stores);
    }

//    店の追加
    @PostMapping
    public ResponseEntity<Store> insert(@RequestBody Store store, UriComponentsBuilder builder){
        Store createdStore = storeService.insert(store);

        URI Location = builder.path("/api/store/{id}").buildAndExpand(createdStore.getComId()).toUri();
        return ResponseEntity.created(Location).build();
    }

//    id:〇番の店に対して、そこに所属しているユーザーの一覧を出す
    @GetMapping("/{id}/all")
    public ResponseEntity<Store> getAllByStoreWithUsers(@PathVariable Integer id){
        Store findStoreWithUsers = storeService.findByStoreWithUsers(id);

        return ResponseEntity.ok(findStoreWithUsers);
    }

    @PutMapping("/{id}/active")
    public ResponseEntity<String> updateStoreStatus(@PathVariable Integer id, @RequestBody Boolean isActive){
        storeService.changeStoreStatus(id, isActive);
        return ResponseEntity.ok(id + " 番の稼働ステータスを " + isActive + " にしました");
    }
}

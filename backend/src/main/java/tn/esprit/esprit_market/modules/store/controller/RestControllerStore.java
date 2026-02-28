package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.dto.StoreDTO;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.mapper.StoreMapper;
import tn.esprit.esprit_market.modules.store.service.IserviceStore;

import java.util.List;
import java.util.stream.Collectors;


@RequestMapping("Store")
@RestController
@AllArgsConstructor
public class RestControllerStore {
    private IserviceStore iserviceStore;
    private StoreMapper storeMapper;
@PostMapping("addstore")
public Store addStore(@RequestBody Store store) {
    return iserviceStore.addStore(store);
}
    // ✅ GET par id
    @GetMapping("get/{id}")
    public StoreDTO getStoreById(@PathVariable Long id) {
        Store store = iserviceStore.getStoreById(id);  // ✅ iserviceStore
        return storeMapper.toDTO(store);
    }

    // ✅ GET all
    @GetMapping("getall")
    public List<StoreDTO> getAllStores() {
        return iserviceStore.getAllStores()             // ✅ iserviceStore
                .stream()
                .map(storeMapper::toDTO)
                .collect(Collectors.toList());
    }
   @PutMapping("update")
    public Store updateStore(  @RequestBody Store store) {
        return iserviceStore.updateStore(store);
    }

    @DeleteMapping("delete/{id}")
    public void deleteStore(@PathVariable Long id ) { iserviceStore.deleteStore(id); }

}

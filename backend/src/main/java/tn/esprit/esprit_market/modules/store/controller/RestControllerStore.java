package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.service.IserviceStore;

import java.util.List;


@RequestMapping("Store")
@RestController
@AllArgsConstructor
public class RestControllerStore {
    private IserviceStore iserviceStore;
@PostMapping("addstore")
public Store addStore(@RequestBody Store store) {
    return iserviceStore.addStore(store);
}
@GetMapping("get/{id}")
    public Store getStoreById( @PathVariable Long id) {
        return iserviceStore.getStoreById(id);
    }
   @GetMapping("all")
    public List<Store> getAllStores() {
        return iserviceStore.getAllStores();
    }

   @PutMapping("update")
    public Store updateStore(  @RequestBody Store store) {
        return iserviceStore.updateStore(store);
    }

  @DeleteMapping("delete")
    public void deleteStore(@RequestBody Store store   ) {
       iserviceStore.deleteStore(store);
    }
}

package tn.esprit.esprit_market.mproduct.service;

import tn.esprit.esprit_market.mproduct.entites.Store;
import tn.esprit.esprit_market.mproduct.repository.IRepositoryStore;

import java.util.List;

public class ServiceStore implements IserviceStore {
    private IRepositoryStore iRepositoryStore;
    @Override
    public Store createStore(Store store) {
        return iRepositoryStore.save(store);
    }

    @Override
    public Store getStoreById(Long id) {
        return iRepositoryStore.findById(id).get();
    }

    @Override
    public List<Store> getAllStores() {
        return iRepositoryStore.findAll();
    }

    @Override
    public Store updateStore(Long id, Store store) {
        return iRepositoryStore.save(store);
    }

    @Override
    public void deleteStore(Store store) {
     iRepositoryStore.delete(store);
    }
}

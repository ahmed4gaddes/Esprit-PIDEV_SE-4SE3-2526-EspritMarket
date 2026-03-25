package tn.esprit.esprit_market.modules.store.service;

import tn.esprit.esprit_market.modules.store.entity.Store;

import java.util.List;


public interface IserviceStore {
    Store addStore(Store store, String email);
    Store getStoreById(Long id);
    List<Store> getAllStores();
    Store updateStore(Store store);
    void deleteStore( Long id );
    List<Store> getMyStores(String email);
}

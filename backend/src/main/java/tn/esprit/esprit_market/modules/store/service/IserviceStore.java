package tn.esprit.esprit_market.modules.store.service;

import tn.esprit.esprit_market.modules.store.entity.Store;

import java.util.List;


public interface IserviceStore {
    Store addStore(Store store);
    Store getStoreById(Long id);
    List<Store> getAllStores();
    Store updateStore(Store store);
    void deleteStore( Store store );
}

package tn.esprit.esprit_market.mproduct.service;
import tn.esprit.esprit_market.mproduct.entites.Store;
import java.util.List;


public interface IserviceStore {
    Store createStore(Store store);
    Store getStoreById(Long id);
    List<Store> getAllStores();
    Store updateStore(Long id, Store store);
    void deleteStore(Store store);
}

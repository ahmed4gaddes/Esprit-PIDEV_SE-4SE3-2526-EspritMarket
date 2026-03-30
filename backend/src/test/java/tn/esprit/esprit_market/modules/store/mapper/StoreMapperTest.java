package tn.esprit.esprit_market.modules.store.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.esprit_market.modules.administration.entity.Commission;
import tn.esprit.esprit_market.modules.administration.entity.Rule;
import tn.esprit.esprit_market.modules.marketing.entity.Advertisement;
import tn.esprit.esprit_market.modules.store.dto.StoreDTO;
import tn.esprit.esprit_market.modules.store.entity.Category;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StoreMapperTest {

    private StoreMapper storeMapper;

    @BeforeEach
    void setUp() {
        storeMapper = new StoreMapper();
    }

    @Test
    void testToDTO_WithAllRelations() {
        User owner = new User();
        owner.setId(99L);
        owner.setName("Owner Name");

        Product product = new Product();
        product.setId(10L);
        product.setName("Product Name");

        Category category = new Category();
        category.setName("Category Name");

        Advertisement ad = new Advertisement();
        ad.setId(20L);
        ad.setTitle("Ad Title");

        Commission comm = new Commission();
        comm.setId(30L);

        Rule rule = new Rule();
        rule.setId(40L);
        rule.setTitle("Rule Title");

        Store store = new Store();
        store.setId(1L);
        store.setName("Store Name");
        store.setDescription("Desc");
        store.setActive(true);
        store.setCreatedAt(new Date());

        store.setOwner(owner);
        store.setProducts(List.of(product));
        store.setCategories(List.of(category));
        store.setAdvertisements(Set.of(ad));
        store.setCommissions(List.of(comm));
        store.setRules(Set.of(rule));

        StoreDTO dto = storeMapper.toDTO(store);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Store Name", dto.getName());
        assertEquals("Desc", dto.getDescription());
        assertTrue(dto.isActive());
        assertNotNull(dto.getCreatedAt());

        assertEquals(99L, dto.getOwnerId());
        assertEquals("Owner Name", dto.getOwnerName());

        assertEquals(1, dto.getProductIds().size());
        assertEquals(10L, dto.getProductIds().get(0));
        assertEquals("Product Name", dto.getProductNames().get(0));

        assertEquals(1, dto.getCategoryNames().size());
        assertEquals("Category Name", dto.getCategoryNames().get(0));

        assertEquals(1, dto.getAdvertisementIds().size());
        assertEquals(20L, dto.getAdvertisementIds().get(0));
        assertEquals("Ad Title", dto.getAdvertisementTitles().get(0));

        assertEquals(1, dto.getCommissionIds().size());
        assertEquals(30L, dto.getCommissionIds().get(0));

        assertEquals(1, dto.getRuleIds().size());
        assertEquals(40L, dto.getRuleIds().get(0));
        assertEquals("Rule Title", dto.getRuleTitles().get(0));
    }

    @Test
    void testToDTO_WithNullRelations() {
        Store store = new Store();
        store.setId(2L);
        store.setName("Empty Store");

        StoreDTO dto = storeMapper.toDTO(store);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertNull(dto.getOwnerId());
        assertTrue(dto.getProductIds().isEmpty());
    }
}

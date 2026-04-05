package tn.esprit.esprit_market.modules.administration.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.esprit_market.modules.administration.dto.CommissionDTO;
import tn.esprit.esprit_market.modules.administration.dto.FinancialReportDTO;
import tn.esprit.esprit_market.modules.administration.dto.RuleDTO;
import tn.esprit.esprit_market.modules.administration.entity.Commission;
import tn.esprit.esprit_market.modules.administration.entity.FinancialReport;
import tn.esprit.esprit_market.modules.administration.entity.Rule;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AdministrationMapperTest {

    private AdministrationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AdministrationMapper();
    }

    @Test
    void testRuleMapping() {
        User creator = new User();
        creator.setId(10L);
        creator.setName("Admin User");

        Store s = new Store();
        s.setId(5L);

        Rule rule = new Rule();
        rule.setId(1L);
        rule.setTitle("Test Rule");
        rule.setMandatory(true);
        rule.setCreatedBy(creator);
        rule.setAppliesTo(Set.of(s));

        RuleDTO dto = mapper.toRuleDTO(rule);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Rule", dto.getTitle());
        assertTrue(dto.isMandatory());
        assertEquals(10L, dto.getCreatedById());
        assertEquals("Admin User", dto.getCreatedByName());
        assertEquals(1, dto.getAppliesToStoreIds().size());

        Rule entity = mapper.toRuleEntity(dto, creator, Set.of(s));
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("Test Rule", entity.getTitle());
        assertTrue(entity.isMandatory());
    }

    @Test
    void testCommissionMapping() {
        Store store = new Store();
        store.setId(10L);
        store.setName("Test Store");

        Commission c = new Commission();
        c.setId(2L);
        c.setRate(5.0);
        c.setAmount(100.0);
        c.setStore(store);

        CommissionDTO dto = mapper.toCommissionDTO(c);
        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals(5.0, dto.getRate());
        assertEquals(100.0, dto.getAmount());
        assertEquals(10L, dto.getStoreId());
        assertEquals("Test Store", dto.getStoreName());

        Commission entity = mapper.toCommissionEntity(dto, store);
        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals(5.0, entity.getRate());
        assertEquals(100.0, entity.getAmount());
    }

    @Test
    void testFinancialReportMapping() {
        Store store = new Store();
        store.setName("FR Store");

        Commission c = new Commission();
        c.setId(10L);
        c.setStore(store);

        FinancialReport rep = new FinancialReport();
        rep.setId(3L);
        rep.setRevenue(1000.0);
        rep.setNet(900.0);
        rep.setCommission(c);
        rep.setDate(new Date());

        FinancialReportDTO dto = mapper.toFinancialReportDTO(rep);
        assertNotNull(dto);
        assertEquals(3L, dto.getId());
        assertEquals(1000.0, dto.getRevenue());
        assertEquals(900.0, dto.getNet());
        assertEquals(10L, dto.getCommissionId());
        assertEquals("FR Store", dto.getStoreName());
        assertNotNull(dto.getDate());

        FinancialReport entity = mapper.toFinancialReportEntity(dto, c);
        assertNotNull(entity);
        assertEquals(3L, entity.getId());
        assertEquals(1000.0, entity.getRevenue());
        assertEquals(900.0, entity.getNet());
    }

    @Test
    void testNulls() {
        assertNull(mapper.toRuleDTO(null));
        assertNull(mapper.toRuleEntity(null, null, null));
        
        assertNull(mapper.toCommissionDTO(null));
        assertNull(mapper.toCommissionEntity(null, null));
        
        assertNull(mapper.toFinancialReportDTO(null));
        assertNull(mapper.toFinancialReportEntity(null, null));
    }
}

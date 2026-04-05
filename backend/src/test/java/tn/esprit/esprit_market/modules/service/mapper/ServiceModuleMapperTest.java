package tn.esprit.esprit_market.modules.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.esprit_market.modules.service.dto.*;
import tn.esprit.esprit_market.modules.service.entity.*;
import tn.esprit.esprit_market.modules.service.enums.ValidationStatus;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ServiceModuleMapperTest {

    private ServiceModuleMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ServiceModuleMapper();
    }

    @Test
    void testServiceMapping() {
        User u = new User();
        u.setId(1L);

        Service s = new Service();
        s.setId(10L);
        s.setTitle("Test Service");
        s.setCreator(u);

        ServiceDTO dto = mapper.toDto(s);
        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals(1L, dto.getCreatorId());

        Service s2 = new Service();
        mapper.toEntity(dto, s2);
        assertEquals("Test Service", s2.getTitle());
    }

    @Test
    void testWorkshopMapping() {
        Workshop w = new Workshop();
        w.setId(20L);
        w.setTitle("Spring Workshop");
        w.setDurationHours(10);
        w.setCapacity(20);

        WorkshopDTO dto = mapper.toDto(w);
        assertNotNull(dto);
        assertEquals(20L, dto.getId());
        assertEquals(10, dto.getDurationHours());

        Workshop w2 = new Workshop();
        mapper.toEntity(dto, w2);
        assertEquals("Spring Workshop", w2.getTitle());
        assertEquals(10, w2.getDurationHours());
    }

    @Test
    void testCertificateMapping() {
        Course c = new Course();
        c.setId(5L);
        c.setCourseOrder(1);

        Certificate cert = new Certificate();
        cert.setId(30L);
        cert.setTitle("AWS Cert");
        cert.setOrganization("Amazon");
        cert.setStatus(ValidationStatus.APPROVED);
        cert.setRequiredCourses(Set.of(c));

        CertificateDTO dto = mapper.toDto(cert);
        assertNotNull(dto);
        assertEquals(30L, dto.getId());
        assertEquals("Amazon", dto.getOrganization());
        assertEquals(ValidationStatus.APPROVED, dto.getStatus());
        assertEquals(1, dto.getCourseIds().size());

        Certificate cert2 = new Certificate();
        mapper.toEntity(dto, cert2);
        assertEquals("AWS Cert", cert2.getTitle());
        assertEquals("Amazon", cert2.getOrganization());
    }

    @Test
    void testInternshipMapping() {
        Internship in = new Internship();
        in.setId(40L);
        in.setCompany("Google");
        in.setDurationMonths(6);
        in.setAgreementSigned(true);

        InternshipDTO dto = mapper.toDto(in);
        assertNotNull(dto);
        assertEquals(40L, dto.getId());
        assertEquals("Google", dto.getCompany());
        assertTrue(dto.isAgreementSigned());

        Internship in2 = new Internship();
        mapper.toEntity(dto, in2);
        assertEquals("Google", in2.getCompany());
        assertTrue(in2.isAgreementSigned());
    }

    @Test
    void testCourseMapping() {
        Course c = new Course();
        c.setId(50L);
        c.setTitle("Java Basics");
        c.setMandatory(true);

        CourseDTO dto = mapper.toDto(c);
        assertNotNull(dto);
        assertEquals(50L, dto.getId());
        assertTrue(dto.isMandatory());

        Course c2 = new Course();
        mapper.toEntity(dto, c2);
        assertEquals("Java Basics", c2.getTitle());
        assertTrue(c2.isMandatory());
    }

    @Test
    void testRegistrationMapping() {
        Registration r = new Registration();
        r.setId(60L);
        r.setAttendanceConfirmed(true);
        r.setEvaluationScore(9);

        RegistrationDTO dto = mapper.toDto(r);
        assertNotNull(dto);
        assertTrue(dto.isAttendanceConfirmed());
        assertEquals(9, dto.getEvaluationScore());

        Registration r2 = new Registration();
        mapper.toEntity(dto, r2);
        assertTrue(r2.isAttendanceConfirmed());
    }

    @Test
    void testServiceCalendarMapping() {
        ServiceCalendar sc = new ServiceCalendar();
        sc.setId(70L);
        sc.setCapacity(50);
        sc.setAvailable(true);

        ServiceCalendarDTO dto = mapper.toDto(sc);
        assertNotNull(dto);
        assertEquals(50, dto.getCapacity());

        ServiceCalendar sc2 = new ServiceCalendar();
        mapper.toEntity(dto, sc2);
        assertTrue(sc2.isAvailable());
    }

    @Test
    void testSupportingDocumentMapping() {
        SupportingDocument doc = new SupportingDocument();
        doc.setId(80L);
        doc.setName("ID.pdf");
        doc.setSize(1024L);

        SupportingDocumentDTO dto = mapper.toDto(doc);
        assertNotNull(dto);
        assertEquals("ID.pdf", dto.getName());

        SupportingDocument doc2 = new SupportingDocument();
        mapper.toEntity(dto, doc2);
        assertEquals("ID.pdf", doc2.getName());
    }

    @Test
    void testCertificateValidationMapping() {
        CertificateValidation cv = new CertificateValidation();
        cv.setId(90L);
        cv.setComment("Looks good");

        CertificateValidationDTO dto = mapper.toDto(cv);
        assertNotNull(dto);
        assertEquals("Looks good", dto.getComment());

        CertificateValidation cv2 = new CertificateValidation();
        mapper.toEntity(dto, cv2);
        assertEquals("Looks good", cv2.getComment());
    }

    @Test
    void testGamificationMapping() {
        Gamification g = new Gamification();
        g.setId(100L);
        g.setPoints(500);
        g.setLevel(5);

        GamificationDTO dto = mapper.toDto(g);
        assertNotNull(dto);
        assertEquals(500, dto.getPoints());

        Gamification g2 = new Gamification();
        mapper.toEntity(dto, g2);
        assertEquals(5, g2.getLevel());
    }

    @Test
    void testNulls() {
        assertNull(mapper.toDto((Service) null));
        assertNull(mapper.toDto((Workshop) null));
        assertNull(mapper.toDto((Certificate) null));
        assertNull(mapper.toDto((Internship) null));
        assertNull(mapper.toDto((Course) null));
        assertNull(mapper.toDto((Registration) null));
        assertNull(mapper.toDto((ServiceCalendar) null));
        assertNull(mapper.toDto((SupportingDocument) null));
        assertNull(mapper.toDto((Gamification) null));

        // Ensure toEntity doesn't throw null pointers
        mapper.toEntity((ServiceDTO) null, new Service());
        mapper.toEntity(new ServiceDTO(), null);
    }
}

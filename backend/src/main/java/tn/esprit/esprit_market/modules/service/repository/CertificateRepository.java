package tn.esprit.esprit_market.modules.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.service.entity.Certificate;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    @Query("SELECT DISTINCT c FROM Certificate c LEFT JOIN FETCH c.requiredCourses")
    List<Certificate> findAllWithCourses();

    @Query("SELECT DISTINCT c FROM Certificate c LEFT JOIN FETCH c.requiredCourses WHERE c.id = :id")
    Optional<Certificate> findByIdWithCourses(@Param("id") Long id);

    /** Certificats qui listent ce cours comme requis (pour affichage / sync). */
    List<Certificate> findByRequiredCourses_Id(Long courseId);
}
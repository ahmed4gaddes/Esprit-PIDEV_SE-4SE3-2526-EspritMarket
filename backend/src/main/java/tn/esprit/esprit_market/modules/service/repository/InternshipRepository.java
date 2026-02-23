package tn.esprit.esprit_market.modules.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.service.entity.Internship;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {
}
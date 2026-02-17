package tn.esprit.esprit_market.Module.service.entities;


import jakarta.persistence.*;
import java.util.Date;

@Entity
public class SupportingDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url; // Link to the file storage
    private String type; // e.g., "PDF", "PNG"
    private Long size;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;

    @ManyToOne
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;

    // Getters and Setters
}
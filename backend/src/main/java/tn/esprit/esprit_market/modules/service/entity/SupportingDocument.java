package tn.esprit.esprit_market.modules.service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "supporting_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportingDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url;
    private String type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;

    private Long size;

    // SupportingDocument *..1 Certificate
    @ManyToOne
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;
}

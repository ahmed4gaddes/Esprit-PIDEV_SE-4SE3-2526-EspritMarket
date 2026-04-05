package tn.esprit.esprit_market.modules.service.dto;

import lombok.Data;
import java.util.Date;

@Data
public class SupportingDocumentDTO {
    private Long id;
    private String name;
    private String url;
    private String type;
    private Date uploadDate;
    private Long size;

    // Relation
    private Long certificateId;
}

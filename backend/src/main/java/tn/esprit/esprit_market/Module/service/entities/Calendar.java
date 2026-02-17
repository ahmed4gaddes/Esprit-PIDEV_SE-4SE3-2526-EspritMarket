package tn.esprit.esprit_market.Module.service.entities;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Calendar {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date startDate;
    private Date endDate;
    private int capacity;
    private boolean available;

    @ManyToOne
    private Service service;
}
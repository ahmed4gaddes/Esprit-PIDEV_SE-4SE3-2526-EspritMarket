package tn.esprit.esprit_market.mproduct.entites;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotBlank
    private String name;
    @NotBlank
    private  String description;
    @NotBlank
    private  boolean active;
    @NotBlank
    private Date createdAt;
    @OneToMany (mappedBy = "store" ,cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    Set<Product> products;

}
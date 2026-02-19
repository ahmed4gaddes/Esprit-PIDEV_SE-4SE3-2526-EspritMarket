package tn.esprit.esprit_market.mproduct.entites;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;
    @Column(nullable = false)
    @NotBlank
    private  String name;
    @NotBlank
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotBlank
    private  CategoryType type;
    @OneToMany(mappedBy = "category")
    private Set<Product> products;
}
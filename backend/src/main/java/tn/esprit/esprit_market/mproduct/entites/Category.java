package tn.esprit.esprit_market.mproduct.entites;

import jakarta.persistence.*;
import lombok.*;

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
    private  String name;
    private String description;
    private  CategoryType type;
    @OneToMany(mappedBy = "category")
    private Set<Product> products;
}
package com.ocena.qlsc.product.model;

import com.ocena.qlsc.common.model.BaseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product extends BaseModel  implements Serializable {

    @Column(name = "product_id", unique = true)
    private Long productId;

    @Column(name = "product_name")
    private String productName;


//    @OneToMany(mappedBy = "product")
//    private List<Order> orders;

//    public Product(Long productId, String productName) {
//        this.productId = productId;
//        this.productName = productName;
//    }
//
//    public Product(Long productId) {
//        this.productId = productId;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == this) {
//            return true;
//        }
//        if (!(obj instanceof Product)) {
//            return false;
//        }
//        Product other = (Product) obj;
//        return Objects.equals(productId, other.productId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(productId);
//    }
}

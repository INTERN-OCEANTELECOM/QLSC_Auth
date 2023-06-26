package com.ocena.qlsc.product.repository;

import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ProductRepository extends BaseRepository<Product> {
    Page<Product> findAll(Pageable pageable);

    boolean existsProductByProductId(Long productId);

//    @Query("SELECT p FROM Product p WHERE CAST(p.productId AS string) LIKE %:keyword1% OR p.productName LIKE %:keyword2%")
//    Page<Product> searchProduct(@Param("keyword1") String keyword1, @Param("keyword2") String keyword2, Pageable pageable);

    @Query(value = "SELECT * FROM product WHERE CONCAT(CAST(product_id AS STRING), ' ', product_name) " +
            "AGAINST (?1 IN BOOLEAN MODE)", nativeQuery = true)
    Page<Product> searchProduct(String keyword, Pageable pageable);
}
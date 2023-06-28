package com.ocena.qlsc.product.repository;

import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.product.model.Product;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends BaseRepository<Product> {
    Page<Product> findAll(Pageable pageable);

    @Override
    @Cacheable(value = "product")
    List<Product> findAll();

    boolean existsProductByProductId(Long productId);

    @Query("SELECT p FROM Product p WHERE CAST(p.productId AS string) LIKE %:keyword1% OR p.productName LIKE %:keyword2%")
    Page<Product> searchProduct(@Param("keyword1") String keyword1, @Param("keyword2") String keyword2, Pageable pageable);

    Optional<Product> findByProductId(Long productId);
}
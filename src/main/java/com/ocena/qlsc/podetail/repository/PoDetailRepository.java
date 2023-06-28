package com.ocena.qlsc.podetail.repository;

import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.podetail.model.PoDetail;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PoDetailRepository extends BaseRepository<PoDetail> {
    Optional<PoDetail> findByPoDetailId(String poDetailId);

}

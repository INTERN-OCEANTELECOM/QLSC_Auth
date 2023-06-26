package com.ocena.qlsc.po.repository;

import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.po.model.Po;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PoRepository extends BaseRepository<Po> {
    Optional<Po> findByPoNumber(String poNumber);

    Optional<Po> findById(String id);
}

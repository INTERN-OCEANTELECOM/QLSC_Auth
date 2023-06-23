package com.ocena.qlsc.po.repository;

import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.po.model.Po;
import org.springframework.stereotype.Repository;

@Repository
public interface PoRepository extends BaseRepository<Po> {
    Po findByPoNumber(String poNumber);
}

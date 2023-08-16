package com.ocena.qlsc.repair_history.repository;

import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.repair_history.model.RepairHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairHistoryRepository extends BaseRepository<RepairHistory> {
//    @Query("""
//                from PoDetail pd where (pd.serialNumber IN :serialNumbers OR :serialNumbers IS EMPTY)
//                AND (pd.po.poNumber IN :poNumbers  OR :poNumbers IS EMPTY)
//                AND (pd.product.productName = :productName OR :productName IS NULL)
//          """)
//    Page<PoDetail> searchRepairHistory(List<String> serialNumbers,
//                                       List<String> poNumbers,
//                                       String productName);

}

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
    @Query("select rh from RepairHistory  rh where rh.poDetail.po.poNumber =:poNumber and rh.poDetail.serialNumber =:serialNumber")
    List<RepairHistory> getRepairHistoriesBySerialNumberAndPoNumber(String serialNumber, String poNumber);

    @Query("""
                SELECT pd FROM PoDetail pd
                WHERE (:serialNumbers IS NULL OR pd.serialNumber IN :serialNumbers)
                AND (:poNumbers IS NULL OR pd.po.poNumber IN :poNumbers)
                AND (:productName IS NULL OR pd.product.productName =:productName)
          """)
    Page<PoDetail> searchRepairHistory(@Param("serialNumbers") List<String> serialNumbers,
                                       @Param("poNumbers") List<String> poNumbers,
                                       @Param("productName") String productName, Pageable pageable);

    @Query("""
                SELECT rh
                FROM RepairHistory rh WHERE rh.poDetail.product.productName = :productName
                AND rh.poDetail.serialNumber = :serialNumber
            """)
    List<RepairHistory> findByProductNameAndSerialNumber(@Param("productName") String productName,
                                                         @Param("serialNumber") String serialNumber);
}

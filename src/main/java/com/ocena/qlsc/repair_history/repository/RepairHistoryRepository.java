package com.ocena.qlsc.repair_history.repository;

import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.repair_history.model.RepairHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairHistoryRepository extends BaseRepository<RepairHistory> {
    @Query("SELECT rh FROM RepairHistory rh " +
            "WHERE " +
            "(rh.poDetail.product.productName LIKE %:keyword1% OR :keyword1 IS NULL)" +
            "AND (rh.creator LIKE %:keyword2% OR :keyword2 IS NULL)" +
            "AND (CAST(rh.repairResults AS string) LIKE %:keyword3% OR :keyword3 IS NULL)")
    Page<RepairHistory> searchRepairHistory(
            @Param("keyword1") String keyword4,
            @Param("keyword2") String keyword5,
            @Param("keyword3") String keyword6,
            Pageable pageable);
}

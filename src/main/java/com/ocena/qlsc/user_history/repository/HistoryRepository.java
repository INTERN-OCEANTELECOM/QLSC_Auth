package com.ocena.qlsc.user_history.repository;

import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.user_history.model.History;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends BaseRepository<History> {
}

package com.ocena.qlsc.po.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.po.dto.PoDTO;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.po.repository.PoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoService extends BaseServiceImpl<Po, PoDTO> implements IPoSerivce {

    @Autowired
    PoRepository poRepository;



    @Override
    protected BaseRepository<Po> getBaseRepository() {
        return poRepository;
    }

    @Override
    protected BaseMapper<Po, PoDTO> getBaseMapper() {
        return null;
    }

    @Override
    protected Page<Po> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
        return null;
    }

    @Override
    protected List<Po> getListSearchResults(String keyword) {
        return null;
    }
}

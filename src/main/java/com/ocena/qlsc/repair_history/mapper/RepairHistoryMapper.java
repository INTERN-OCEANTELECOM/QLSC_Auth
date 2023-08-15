package com.ocena.qlsc.repair_history.mapper;

import com.ocena.qlsc.common.model.BaseMapperImpl;
import com.ocena.qlsc.repair_history.dto.RepairHistoryDto;
import com.ocena.qlsc.repair_history.model.RepairHistory;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class RepairHistoryMapper extends BaseMapperImpl<RepairHistory, RepairHistoryDto> {
    public RepairHistoryMapper(ModelMapper modelMapper) {
        super(modelMapper);
    }

    @Override
    protected Class<RepairHistory> getEntityClass() {
        return RepairHistory.class;
    }

    @Override
    protected Class<RepairHistoryDto> getDtoClass() {
        return RepairHistoryDto.class;
    }
}

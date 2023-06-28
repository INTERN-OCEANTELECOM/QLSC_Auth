package com.ocena.qlsc.podetail.dto;

import com.ocena.qlsc.common.constants.GlobalConstants;
import com.ocena.qlsc.po.dto.PoDTO;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.podetail.status.RepairCategory;
import com.ocena.qlsc.podetail.status.configEnum.EnumValue;
import com.ocena.qlsc.product.dto.ProductDTO;
import com.ocena.qlsc.product.model.Product;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@Builder
public class PoDetailRequest {
    private String poDetailId;
    private ProductDTO product;
    private String serialNumber;
    private PoDTO po;
    private String bbbgNumber;
    private Long importDate;
    @NotNull
    @Min(value = 0, message = "Giá trị không hợp lệ")
    @Max(value = RepairCategory.LENGTH, message = "Giá trị không hợp lệ")
    private int repairCategory;

    @Override
    public String toString() {
        return "PoDetailRequest{" +
                "product=" + product +
                ", serialNumber='" + serialNumber + '\'' +
                ", po=" + po +
                ", bbbgNumber='" + bbbgNumber + '\'' +
                ", importDate=" + importDate +
                ", repairCategory=" + repairCategory +
                '}';
    }
}

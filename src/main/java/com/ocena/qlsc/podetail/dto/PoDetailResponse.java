package com.ocena.qlsc.podetail.dto;

import com.ocena.qlsc.po.dto.PoDTO;
import com.ocena.qlsc.podetail.enums.ExportPartner;
import com.ocena.qlsc.podetail.enums.KSCVT;
import com.ocena.qlsc.podetail.enums.RepairCategory;
import com.ocena.qlsc.podetail.enums.RepairStatus;
import com.ocena.qlsc.product.dto.ProductDTO;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoDetailResponse {
    private String poDetailId;
    private ProductDTO product;
    private String serialNumber;
    private PoDTO po;
    private String bbbgNumber;
    private Long importDate;

    @Min(value = 0, message = "Giá trị không hợp lệ")
    @Max(value = RepairCategory.LENGTH - 1, message = "Giá trị không hợp lệ")
    private Short repairCategory;

    @Min(value = 0, message = "Giá trị Trạng Thái SC không hợp lệ")
    @Max(value = RepairStatus.LENGTH - 1, message = "Giá trị Trạng Thái SC không hợp lệ")
    private Short repairStatus;

    @Min(value = 0, message = "Giá trị Trạng Thái SC không hợp lệ")
    @Max(value = ExportPartner.LENGTH - 1, message = "Giá trị Trạng Thái SC không hợp lệ")
    private Short exportPartner;

    @Min(value = 0, message = "Giá trị Trạng Thái SC không hợp lệ")
    @Max(value = KSCVT.LENGTH - 1, message = "Giá trị Trạng Thái SC không hợp lệ")
    private Short kcsVT;
    @Min(value = 16782084000L, message = "Giá trị Trạng Thái SC không hợp lệ")
    private Long warrantyPeriod;

    @Override
    public String toString() {
        return "PoDetailRequest{" +
                "poDetailId='" + poDetailId + '\'' +
                ", product=" + product +
                ", serialNumber='" + serialNumber + '\'' +
                ", po=" + po +
                ", bbbgNumber='" + bbbgNumber + '\'' +
                ", importDate=" + importDate +
                ", repairCategory=" + repairCategory +
                ", repairStatus=" + repairStatus +
                '}';
    }
}

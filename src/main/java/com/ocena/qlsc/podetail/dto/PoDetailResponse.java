package com.ocena.qlsc.podetail.dto;

import com.ocena.qlsc.po.dto.PoDTO;
import com.ocena.qlsc.podetail.enums.*;
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
    @Min(value = 0, message = "Giá trị hạng muc không hợp lệ")
    @Max(value = RepairCategory.LENGTH - 1, message = "Giá trị hạng mục không hợp lệ")
    private Short repairCategory;
    @Min(value = 0, message = "Giá trị trạng thái SC không hợp lệ")
    @Max(value = RepairStatus.LENGTH - 1, message = "Giá trị trạng thái SC không hợp lệ")
    private Short repairStatus;
    private Long exportPartner;
    @Min(value = 0, message = "Giá trị KSC không hợp lệ")
    @Max(value = KSCVT.LENGTH - 1, message = "Giá trị KSC không hợp lệ")
    private Short kcsVT;
    private Long warrantyPeriod;
    @Min(value = 0, message = "Giá trị ưu tiên không hợp lệ")
    @Max(value = Priority.LENGTH - 1, message = "Giá trị ưu tiên không hợp lệ")
    private Short priority;
    private String bbbgNumberExport;
    private String note;

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

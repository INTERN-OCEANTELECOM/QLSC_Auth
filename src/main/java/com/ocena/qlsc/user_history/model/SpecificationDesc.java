package com.ocena.qlsc.user_history.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SpecificationDesc {
    private String amount;
    private String record;
    private String keyUpdate;
    private String description = "";

    public SpecificationDesc(String amount, String record) {
        this.amount = "Số lượng: " + amount + "\n";
        this.record = "Key: " + record + "\n";
    }

    public SpecificationDesc(String amount, String record, String description) {
        this.amount = "Số lượng: " + amount + "\n";
        this.record = "Key: " + record + "\n";
        this.description = "Chi tiết: " + description + "\n";
    }

    // Set Description to edit data
    public void setDescription(List<String> fields, List<String> oldDatas, List<String> newDatas) {
        String result = fields.size() > 0 ? "- " : "";

        for(int i = 0; i < fields.size(); i++) {
            result += fields.get(i) + ": từ <" + oldDatas.get(i) + "> thành <" + newDatas.get(i) + ">; ";
        }
        this.description = result;
    }

    // Set description to add data
    public void setDescription(List<String> fields, List<String> newDatas) {
        String result = fields.size() > 0 ? "- " : "";
        for(int i = 0; i < fields.size(); i++) {
            result += fields.get(i) + ": <" + newDatas.get(i) + ">; ";
        }
        this.description = result;
    }

    // Set description to import data excel
    public void setDescription(String data) {
        this.description = data != "" ? "Số serial: " + data : "";
    }

    // Set description to update data excel
    public void setDescription(List<String> fields, List<String> oldDatas, List<String> newDatas, String key) {
        String result = fields.size() > 0 ? "- " + keyUpdate : "";
        for(int i = 0; i < fields.size(); i++) {
            result += fields.get(i) + ": từ <" + oldDatas.get(i) + "> thành <" + newDatas.get(i) + ">; ";
        }
        this.description = result;
    }

    public String getSpecification() {
        return amount + record + "Chi tiết: \n" +description;
    }
}

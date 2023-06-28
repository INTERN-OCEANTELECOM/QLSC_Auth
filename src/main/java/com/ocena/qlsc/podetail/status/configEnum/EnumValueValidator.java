package com.ocena.qlsc.podetail.status.configEnum;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<EnumValue, Enum<?>> {
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumValue annotation) {
        this.enumClass = annotation.enumClass();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Giá trị null được cho phép, có thể yêu cầu bổ sung logic kiểm tra nếu cần thiết.
        }

        for (Enum<?> enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().equals(value.name())) {
                return true; // Tìm thấy giá trị enum trong enum class.
            }
        }
        return false; // Không tìm thấy giá trị enum trong enum class.
    }
}

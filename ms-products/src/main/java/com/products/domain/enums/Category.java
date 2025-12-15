package com.products.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.products.infrastructure.exception.InvalidCategoryException;

public enum Category {
    ELECTRONICS,
    FOOD,
    BOOKS,
    OTHER;

    @JsonCreator
    public static Category fromString(String value) {
        try {
            return Category.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCategoryException(value);
        }
    }
}

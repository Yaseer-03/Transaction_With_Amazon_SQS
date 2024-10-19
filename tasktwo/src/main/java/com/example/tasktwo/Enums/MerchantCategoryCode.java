package com.example.tasktwo.Enums;

import java.util.Arrays;

public enum MerchantCategoryCode {

    GROCERY_STORES(5411),
    EATING_PLACES(5812),
    FAST_FOOD_RESTAURANTS(5814),
    DEPARTMENT_STORES(5311),
    GAS_STATIONS(5541),
    HOTELS(7011),
    TRAVEL_AGENCIES(4722);

    private final int code;

    MerchantCategoryCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static boolean isValid(int code) {
        return Arrays.stream(MerchantCategoryCode.values())
                .anyMatch(mcc -> mcc.getCode() == code);
    }

}

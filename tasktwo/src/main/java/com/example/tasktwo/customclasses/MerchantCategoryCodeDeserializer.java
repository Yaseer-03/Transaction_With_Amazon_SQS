package com.example.tasktwo.customclasses;

import com.example.tasktwo.Enums.MerchantCategoryCode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class MerchantCategoryCodeDeserializer extends JsonDeserializer<MerchantCategoryCode> {

    @Override
    public MerchantCategoryCode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().toUpperCase();
        try {
            return MerchantCategoryCode.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid Merchant Category Code: " + value);
        }
    }
}


package com.example.tasktwo.Service;

import com.example.tasktwo.Entity.Merchant;
import com.example.tasktwo.Enums.MerchantCategoryCode;
import com.example.tasktwo.Repository.MerchantRepository;
import com.example.tasktwo.customclasses.Validations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;


@Service
public class MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private Validations validator;

    public String addMerchant(Merchant merchant) {
        // Ensure merchantId is correctly set and valid
        if (merchant.getMerchantId() == 0 || String.valueOf(merchant.getMerchantId()).length() != 3) {
            throw new IllegalArgumentException("Invalid Merchant ID.");
        }

        // Check if the merchant ID already exists
        if (merchantRepository.existsById(merchant.getMerchantId())) {
            throw new IllegalArgumentException("Merchant ID already exists.");
        }

        // Validate the merchant object
        validateMerchant(merchant);

        // Save the merchant if all validations pass
        merchantRepository.save(merchant);
        return "Merchant registered successfully";
    }

    private void validateMerchant(Merchant merchant) {
        if (isNull(merchant)) {
            throw new IllegalArgumentException("Merchant ID, name, and email are required.");
        }

        if(!validator.isEmailValid(merchant.getMerchantEmail()))
            throw new IllegalArgumentException("Enter a valid email");

        if (!validateMCC(merchant.getMerchantCategoryCode())) {
            throw new IllegalArgumentException("Enter a valid Merchant Category Code (MCC).");
        }

        if(merchantRepository.existsByMerchantEmail(merchant.getMerchantEmail())){
            throw new IllegalArgumentException("Email already in use");
        }
    }

    private static boolean isNull(Merchant merchant) {
        return merchant.getMerchantName() == null || merchant.getMerchantName().trim().isEmpty() ||
                merchant.getMerchantEmail() == null || merchant.getMerchantEmail().trim().isEmpty() ||
                merchant.getMerchantId() == 0;
    }

    private boolean validateMCC(MerchantCategoryCode mcc) {
        // Validate merchant category code is one of the predefined valid MCCs
        return mcc != null && (
                mcc == MerchantCategoryCode.EATING_PLACES ||
                        mcc == MerchantCategoryCode.GROCERY_STORES ||
                        mcc == MerchantCategoryCode.FAST_FOOD_RESTAURANTS ||
                        mcc == MerchantCategoryCode.DEPARTMENT_STORES ||
                        mcc == MerchantCategoryCode.GAS_STATIONS ||
                        mcc == MerchantCategoryCode.HOTELS ||
                        mcc == MerchantCategoryCode.TRAVEL_AGENCIES);
    }


    public String deleteMerchant(long merchantId) {
        // check if the merchant exists with the given id
        Merchant checkingMerchantExistWithId = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new NoSuchElementException("Merchant not found with ID " + merchantId));

        // if the merchant found, delete the merchant
        merchantRepository.delete(checkingMerchantExistWithId);
        return "Merchant deleted successfully";
    }
}

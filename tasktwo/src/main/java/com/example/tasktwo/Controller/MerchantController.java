package com.example.tasktwo.Controller;

import com.example.tasktwo.Entity.Merchant;
import com.example.tasktwo.Service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

   @Autowired
   private MerchantService merchantService;

    @PostMapping("/registerMerchant")
    public String creatingUser(@RequestBody Merchant merchant){
        return merchantService.addMerchant(merchant);
    }

    @DeleteMapping("/{merchantId}")
    public String deletingMerchant(@PathVariable long merchantId) {
        return merchantService.deleteMerchant(merchantId);
    }
}

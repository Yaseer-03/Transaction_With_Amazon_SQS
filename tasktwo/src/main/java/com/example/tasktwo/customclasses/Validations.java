package com.example.tasktwo.customclasses;

import org.springframework.stereotype.Component;

@Component
public class Validations {

    // Validating email id
    public Boolean isEmailValid(String email){
        return email.contains("@");
    }
}

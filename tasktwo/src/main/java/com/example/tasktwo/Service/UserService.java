package com.example.tasktwo.Service;

import com.example.tasktwo.customclasses.Validations;
import com.example.tasktwo.Entity.User;
import com.example.tasktwo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Validations validator;

    public String addUser(User user) {

        // Check if the user object is null, username, email
        if (user == null || user.getUserName() == null || user.getEmail() == null ) {
            return "User cannot be null.";
        }

        // validating the email
        if (!validator.isEmailValid(user.getEmail())) return "Invalid email " + user.getEmail();

        // Checking whether the email is present or not
        if (userRepository.existsByEmail(user.getEmail())) return "Email is already in use!";

        userRepository.save(user);

        return "User registered successful";
    }

    public String deleteUser(Long userId) {

        User checkingUserExistWithId = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " not found"));

        userRepository.delete(checkingUserExistWithId);
        return "User deleted successfully";
    }
}

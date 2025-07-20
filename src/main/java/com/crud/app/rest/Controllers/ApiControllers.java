package com.crud.app.rest.Controllers;

import com.crud.app.rest.Models.User;
import com.crud.app.rest.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ApiControllers {

    @Autowired
    private UserRepo userRepo;

    @GetMapping(value = "/")
    public String getPage() {
        return "Welcome";
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.notFound().build(); // This is the 404 you are getting
        }
    }

    @PostMapping(value = "/save")
    public ResponseEntity<User> saveUser(@RequestBody User user){ // Change return type to ResponseEntity<User>
        User savedUser = userRepo.save(user); // Save the user
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED); // Return the saved user and a 201 Created status
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody User user){
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isPresent()) {
            User updateUser = userOptional.get();
            // Update fields from the incoming 'user' object
            updateUser.setFirstName(user.getFirstName());
            updateUser.setLastName(user.getLastName());
            updateUser.setOccupation(user.getOccupation());
            updateUser.setAge(user.getAge());
            User savedUser = userRepo.save(updateUser);
            return ResponseEntity.ok(savedUser);
        } else {
            return ResponseEntity.notFound().build(); // This is the 404 you are getting
        }
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepo.existsById(id)) { // Check if user exists before deleting
            userRepo.deleteById(id);
            return ResponseEntity.noContent().build(); // 204 No Content for successful delete
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found if user doesn't exist
        }
    }

}

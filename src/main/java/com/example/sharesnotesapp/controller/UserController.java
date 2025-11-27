package com.example.sharesnotesapp.controller;

import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.UserMapper;
import com.example.sharesnotesapp.model.dto.request.UserNameDto;
import com.example.sharesnotesapp.model.dto.response.UserResponseDto;
import com.example.sharesnotesapp.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @Autowired
    public UserController(UserService userService, UserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);

        return ResponseEntity.ok(mapper.toDto(user));
    }

    @GetMapping("/email")
    public ResponseEntity<UserResponseDto> findUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(mapper.toDto(userService.getUserByEmail(email)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateCredentials(@PathVariable Long id, @Valid @RequestBody UserNameDto userRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            User updatedUser = userService.updateUserCredentials(id, userRequestDto);

            return ResponseEntity.ok(mapper.toDto(updatedUser));
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            userService.deleteUser(id);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/friends")
    public ResponseEntity<List<UserResponseDto>> getUserFriends(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user){
            List<User> userFriends = userService.getUserFriends(user);

            return ResponseEntity.ok(userFriends.stream().map(mapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUsers(@RequestParam(defaultValue = "") String searchString){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user){
            List<User> users = userService.searchUsers(searchString, user.getId());

            return ResponseEntity.ok(users.stream().map(mapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/friends/search")
    public ResponseEntity<List<UserResponseDto>> searchUserFriends(@RequestParam(defaultValue = "") String searchString){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user){
            List<User> users = userService.searchUserFriends(searchString, user.getId());

            return ResponseEntity.ok(users.stream().map(mapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/non-friends")
    public ResponseEntity<List<UserResponseDto>> searchNonFriends(@RequestParam(defaultValue = "") String searchString){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user){
            List<User> users = userService.searchUsersNotFriends(searchString, user);

            return ResponseEntity.ok(users.stream().map(mapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }
}

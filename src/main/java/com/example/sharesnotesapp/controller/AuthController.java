package com.example.sharesnotesapp.controller;

import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.UserMapper;
import com.example.sharesnotesapp.model.dto.request.UserLoginDto;
import com.example.sharesnotesapp.model.dto.request.UserRequestDto;
import com.example.sharesnotesapp.model.dto.response.UserLoginJwtDto;
import com.example.sharesnotesapp.security.jwt.JwtUtils;
import com.example.sharesnotesapp.service.user.UserService;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping
@Getter
public class AuthController {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserMapper mapper;

    @Autowired
    public AuthController(UserService userService, JwtUtils jwtUtils, AuthenticationManager authenticationManager, UserMapper mapper) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.mapper = mapper;
    }
    @PostMapping("/register")
    public ResponseEntity<User> createAccount(@Valid @RequestBody UserRequestDto userRequestDto) {
        URI uri = URI.create((ServletUriComponentsBuilder.fromCurrentContextPath().path("/register").toUriString()));
        return ResponseEntity.created(uri).body(userService.saveUser(userRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginJwtDto> login(@RequestBody UserLoginDto userLoginDto) {
        try {
            Authentication authenticate = getAuthenticationManager()
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    userLoginDto.getEmail(), userLoginDto.getPassword()
                            )
                    );

            final User user = (User) authenticate.getPrincipal();

            return ResponseEntity.ok().body(
                    UserLoginJwtDto
                            .builder()
                            .tokenValue(getJwtUtils().generateJwtCookie(user))
                            .userInfo(mapper.toDto(user))
                            .build()
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

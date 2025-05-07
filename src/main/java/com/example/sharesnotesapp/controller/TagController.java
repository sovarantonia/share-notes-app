package com.example.sharesnotesapp.controller;

import com.example.sharesnotesapp.model.Tag;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.TagMapper;
import com.example.sharesnotesapp.model.dto.response.TagResponseDto;
import com.example.sharesnotesapp.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    private final TagMapper tagMapper;

    @GetMapping()
    public ResponseEntity<List<TagResponseDto>> getAllTagsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            Set<Tag> tags = tagService.findTagsForUser(user);

            return ResponseEntity.ok(tags.stream().map(tagMapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }
}

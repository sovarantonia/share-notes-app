package com.example.sharesnotesapp.service.tag;

import com.example.sharesnotesapp.model.Tag;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.TagRequestDto;
import com.example.sharesnotesapp.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    public Set<Tag> findOrCreateTagsForUser(User user, Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }

        return tagNames.stream()
                .map(name -> tagRepository.findByUserAndName(user, name)
                        .orElseGet(() -> tagRepository.save(new Tag(name, user))))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Tag> findTagsForUser(User user) {
        return tagRepository.findByUser(user);
    }


}

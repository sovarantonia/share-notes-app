package com.example.sharesnotesapp.service.tag;

import com.example.sharesnotesapp.model.Tag;
import com.example.sharesnotesapp.model.User;
import java.util.Set;

public interface TagService {
    Set<Tag> findOrCreateTagsForUser(User user, Set<String> tagNames);
    Set<Tag> findTagsForUser(User user);
}

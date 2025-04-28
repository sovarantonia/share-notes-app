package com.example.sharesnotesapp.repository;

import com.example.sharesnotesapp.model.Tag;
import com.example.sharesnotesapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByUserAndName(User user, String name);
    Set<Tag> findByUser(User user);
}

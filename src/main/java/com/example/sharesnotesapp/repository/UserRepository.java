package com.example.sharesnotesapp.repository;

import com.example.sharesnotesapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.id <> :currentUserId AND " +
            "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY u.lastName ASC")
    List<User> searchUsersExcludingCurrent(@Param("currentUserId") Long currentUserId,
                                           @Param("searchTerm") String searchTerm);
    List<User> findByIdNot(Long currentUserId);
    @Query("""
    SELECT u FROM User u
    WHERE u.id != :userId AND u.id NOT IN (
        SELECT f.id FROM User u2 JOIN u2.friendList f WHERE u2.id = :userId
    )
""")
    List<User> getUsersNotFriendsWith(@Param("userId") Long userId);

    @Query("""
    SELECT u FROM User u
    WHERE u.id != :userId
      AND u.id NOT IN (
        SELECT f.id FROM User u2 JOIN u2.friendList f WHERE u2.id = :userId
      )
      AND (
        LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
        LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
        LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
      )
""")
    List<User> searchUsersNotFriendsWith(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);
}

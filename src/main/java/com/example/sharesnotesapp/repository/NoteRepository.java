package com.example.sharesnotesapp.repository;

import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.Tag;
import com.example.sharesnotesapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> getNotesByUserOrderByDateDesc(User user);
    List<Note> findAllByUserAndTitleContainsIgnoreCaseOrderByDateDesc(User user, String string);
    List<Note> getFirst5ByUserOrderByDateDesc(User user);
    List<Note> getNotesByUserAndDateBetweenOrderByDateAsc(User user,LocalDate startDate, LocalDate endDate);
    List<Note> findDistinctByUserAndTagsNameIn(User user, List<String> tagNames);
    @Query("""
    SELECT DISTINCT n FROM Note n
    LEFT JOIN n.tags t
    WHERE n.user.id = :userId
    AND (:title IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :title, '%')))
    AND (:tag IS NULL OR t.name = :tag)
    AND (:grade IS NULL OR n.grade = :grade)
    AND (:from IS NULL OR n.date >= :from)
    AND (:to IS NULL OR n.date <= :to)
    ORDER BY n.date DESC
""")
    List<Note> search(
            @Param("userId") Long userId,
            @Param("title") String title,
            @Param("tag") String tag,
            @Param("grade") Integer grade,
            @Param("from") Date from,
            @Param("to") Date to
    );
}

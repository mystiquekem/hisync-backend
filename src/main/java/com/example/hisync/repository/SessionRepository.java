package com.example.hisync.repository;

import com.example.hisync.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    // For members: sessions where they are in the lineup
    @Query("""
        SELECT s FROM Session s
        JOIN s.lineup l
        JOIN l.members lm
        WHERE lm.user.id = :userId
        AND s.date BETWEEN :from AND :to
        ORDER BY s.date ASC
        """)
    List<Session> findByMemberUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // For leaders: all sessions for a band in a date range
    @Query("""
        SELECT s FROM Session s
        WHERE s.band.id = :bandId
        AND s.date BETWEEN :from AND :to
        ORDER BY s.date ASC
        """)
    List<Session> findByBandIdAndDateBetween(
            @Param("bandId") Long bandId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("SELECT s FROM Session s WHERE s.band.id = :bandId ORDER BY s.date DESC")
    List<Session> findByBandId(@Param("bandId") Long bandId);
}
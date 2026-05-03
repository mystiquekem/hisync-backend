// repository/SessionRepository.java
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

    @Query("""
        SELECT s FROM Session s
        JOIN s.members m
        WHERE m.user.id = :userId
        AND s.date BETWEEN :from AND :to
        """)
    List<Session> findByMemberUserIdAndDateBetween(
        @Param("userId") Long userId,
        @Param("from")   LocalDateTime from,
        @Param("to")     LocalDateTime to
    );
}
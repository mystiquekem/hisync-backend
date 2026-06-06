package com.example.hisync.repository;

import com.example.hisync.model.Band;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BandRepository extends JpaRepository<Band, Long> {
    Optional<Band> findByInviteCode(String inviteCode);

    @Query("""
        SELECT b FROM Band b
        JOIN b.members m
        WHERE m.user.id = :userId
        """)
    List<Band> findByMemberUserId(@Param("userId") Long userId);
}
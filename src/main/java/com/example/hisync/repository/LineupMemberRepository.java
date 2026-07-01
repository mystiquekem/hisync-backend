package com.example.hisync.repository;

import com.example.hisync.model.LineupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LineupMemberRepository
        extends JpaRepository<LineupMember, LineupMember.LineupMemberId> {

    @Query("SELECT lm FROM LineupMember lm JOIN FETCH lm.user WHERE lm.id.lineupId = :lineupId")
    List<LineupMember> findByLineupId(@Param("lineupId") Long lineupId);

    @Modifying
    @Query("DELETE FROM LineupMember lm WHERE lm.id.lineupId = :lineupId")
    void deleteByLineupId(@Param("lineupId") Long lineupId);
}
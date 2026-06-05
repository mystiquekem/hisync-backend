package com.example.hisync.repository;

import com.example.hisync.model.BandMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BandMemberRepository extends JpaRepository<BandMember, BandMember.BandMemberId> {

    @Query("SELECT bm FROM BandMember bm JOIN FETCH bm.user WHERE bm.id.bandId = :bandId")
    List<BandMember> findByBandId(@Param("bandId") Long bandId);

    @Query("SELECT bm FROM BandMember bm WHERE bm.id.bandId = :bandId AND bm.id.userId = :userId")
    Optional<BandMember> findByBandIdAndUserId(@Param("bandId") Long bandId, @Param("userId") Long userId);

    @Query("SELECT COUNT(bm) > 0 FROM BandMember bm WHERE bm.id.bandId = :bandId AND bm.id.userId = :userId")
    boolean existsByBandIdAndUserId(@Param("bandId") Long bandId, @Param("userId") Long userId);
}
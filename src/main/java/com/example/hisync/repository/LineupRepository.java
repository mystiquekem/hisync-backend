package com.example.hisync.repository;

import com.example.hisync.model.Lineup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LineupRepository extends JpaRepository<Lineup, Long> {

    @Query("SELECT l FROM Lineup l WHERE l.band.id = :bandId ORDER BY l.createdAt DESC")
    List<Lineup> findByBandId(@Param("bandId") Long bandId);
}
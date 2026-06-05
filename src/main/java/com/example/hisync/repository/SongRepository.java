package com.example.hisync.repository;

import com.example.hisync.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByBandId(Long bandId);
    boolean existsByBandIdAndYoutubeId(Long bandId, String youtubeId);
}
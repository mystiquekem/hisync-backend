package com.example.hisync.repository;

import com.example.hisync.model.UserInstrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserInstrumentRepository
        extends JpaRepository<UserInstrument, UserInstrument.UserInstrumentId> {

    @Query("SELECT ui.id.instrument FROM UserInstrument ui WHERE ui.id.userId = :userId")
    List<String> findInstrumentsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM UserInstrument ui WHERE ui.id.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
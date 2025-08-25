package com.gesture.backend.repository;

import com.gesture.backend.entity.Telemetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {

    List<Telemetry> findByUserIdOrderByTimestampDesc(UUID userId);

    List<Telemetry> findByEventTypeOrderByTimestampDesc(String eventType);

    @Query("SELECT t FROM Telemetry t WHERE t.user.id = :userId AND t.timestamp >= :since ORDER BY t.timestamp DESC")
    List<Telemetry> findByUserIdAndTimestampAfter(@Param("userId") UUID userId, @Param("since") LocalDateTime since);

    @Modifying
    @Query("DELETE FROM Telemetry t WHERE t.timestamp < :before")
    void deleteOldTelemetry(@Param("before") LocalDateTime before);
}

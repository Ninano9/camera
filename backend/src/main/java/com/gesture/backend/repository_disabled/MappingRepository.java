package com.gesture.backend.repository;

import com.gesture.backend.entity.Mapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MappingRepository extends JpaRepository<Mapping, UUID> {

    List<Mapping> findByProfileIdAndEnabledTrueOrderByPriorityAsc(UUID profileId);

    @Query("SELECT m FROM Mapping m WHERE m.profile.id = :profileId AND m.enabled = true ORDER BY m.priority ASC")
    List<Mapping> findActiveMappingsByProfileId(@Param("profileId") UUID profileId);

    @Query("SELECT m FROM Mapping m WHERE m.profile.user.id = :userId AND m.enabled = true ORDER BY m.priority ASC")
    List<Mapping> findActiveMappingsByUserId(@Param("userId") UUID userId);

    boolean existsByProfileIdAndNameAndEnabledTrue(UUID profileId, String name);
}

package com.gesture.backend.repository;

import com.gesture.backend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    List<Profile> findByUserIdAndIsActiveTrue(UUID userId);

    @Query("SELECT p FROM Profile p WHERE p.user.id = :userId AND p.isDefault = true AND p.isActive = true")
    Optional<Profile> findDefaultProfileByUserId(@Param("userId") UUID userId);

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.mappings WHERE p.id = :id AND p.isActive = true")
    Optional<Profile> findByIdWithMappings(@Param("id") UUID id);

    @Query("SELECT p FROM Profile p WHERE p.user.id = :userId AND p.name = :name AND p.isActive = true")
    Optional<Profile> findByUserIdAndName(@Param("userId") UUID userId, @Param("name") String name);

    boolean existsByUserIdAndNameAndIsActiveTrue(UUID userId, String name);
}

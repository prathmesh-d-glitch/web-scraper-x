package com.prats.webscraperx.repository;


import com.prats.webscraperx.model.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EntityRepository extends JpaRepository<Entity, Long> {

    @Query("SELECT o FROM Entity o WHERE o.basePath = ?1")
    List<Entity> findByBasePath(String basePath);
}

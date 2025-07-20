package com.prats.webscraperx.repository;

import com.prats.webscraperx.model.Pattern;

public interface PatternRepository extends JpaRepository<Pattern, Long> {

    // Define custom query methods if needed
    // For example, to find patterns by a specific attribute:
    // List<Pattern> findByAttribute(String attribute);

    // You can also use @Query annotations for more complex queries
}

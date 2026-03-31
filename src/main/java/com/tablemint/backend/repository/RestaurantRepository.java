package com.tablemint.backend.repository;

import com.tablemint.backend.entity.Restaurant;
import com.tablemint.backend.enums.CuisineType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, String>,
        JpaSpecificationExecutor<Restaurant> {

    List<Restaurant> findByCityIgnoreCaseAndActiveTrue(String city);

    List<Restaurant> findByLocalityIgnoreCaseAndActiveTrue(String locality);

    @Query("SELECT r FROM Restaurant r JOIN r.cuisines c WHERE c = :cuisine AND r.active = true")
    List<Restaurant> findByCuisine(@Param("cuisine") CuisineType cuisine);

    @Query("SELECT r FROM Restaurant r WHERE r.active = true ORDER BY r.averageRating DESC")
    List<Restaurant> findTopRated(Pageable pageable);
}
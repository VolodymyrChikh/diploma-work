package com.volodymyrchikh.abitandstudhelp.repository;

import com.volodymyrchikh.abitandstudhelp.domain.MediaResource;
import com.volodymyrchikh.abitandstudhelp.common.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MediaResourceRepository extends JpaRepository<MediaResource, UUID> {

    @Query("SELECT m FROM MediaResource m WHERE m.category.name = :categoryName")
    Page<MediaResource> findAllByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

    @Query("SELECT m FROM MediaResource m WHERE m.category.name = :categoryName AND m.type = :type")
    Page<MediaResource> findAllByCategoryNameAndType(@Param("categoryName") String categoryName, @Param("type") ResourceType type, Pageable pageable);

    @Query("SELECT m FROM MediaResource m WHERE m.type = :type")
    Page<MediaResource> findAllByType(@Param("type") ResourceType type, Pageable pageable);

    @Query("SELECT m FROM MediaResource m WHERE lower(m.title) LIKE lower(concat('%', :query, '%')) OR lower(m.description) LIKE lower(concat('%', :query, '%'))")
    Page<MediaResource> search(@Param("query") String query, Pageable pageable);

    @Query("SELECT m FROM MediaResource m ORDER BY m.createdAt DESC")
    List<MediaResource> findTop5ByOrderByCreatedAtDesc(Pageable pageable);
}

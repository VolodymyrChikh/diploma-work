package com.volodymyrchikh.abitandstudhelp.repository;

import com.volodymyrchikh.abitandstudhelp.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, QuerydslPredicateExecutor<Post> {
    boolean existsBySlug(String slug);
    Optional<Post> findBySlug(String slug);
}

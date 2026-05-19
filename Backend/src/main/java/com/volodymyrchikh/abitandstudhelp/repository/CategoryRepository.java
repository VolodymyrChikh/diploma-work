package com.volodymyrchikh.abitandstudhelp.repository;

import com.volodymyrchikh.abitandstudhelp.common.CategoryType;
import com.volodymyrchikh.abitandstudhelp.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, QuerydslPredicateExecutor<Category> {
    Optional<Category> findByName(String name);

    Optional<Category> findByIdAndType(Long id, CategoryType type);

    List<Category> findAllByTypeOrderByIdAsc(CategoryType type);
}

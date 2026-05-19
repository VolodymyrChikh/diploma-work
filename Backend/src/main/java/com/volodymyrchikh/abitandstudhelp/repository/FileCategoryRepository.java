package com.volodymyrchikh.abitandstudhelp.repository;

import com.volodymyrchikh.abitandstudhelp.domain.FileCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileCategoryRepository extends JpaRepository<FileCategory, Long> {

    List<FileCategory> findAllByOrderByIdAsc();
}


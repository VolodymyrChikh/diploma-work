package com.volodymyrchikh.abitandstudhelp.repository;

import com.volodymyrchikh.abitandstudhelp.domain.AcademicGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcademicGroupRepository extends JpaRepository<AcademicGroup, Long> {

    List<AcademicGroup> findAllByOrderByIdAsc();

    java.util.Optional<AcademicGroup> findByName(String name);
}


package com.volodymyrchikh.abitandstudhelp.repository;

import com.volodymyrchikh.abitandstudhelp.domain.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long>, QuerydslPredicateExecutor<Specialty> {

    Optional<Specialty> findByName(String specialtyName);
}

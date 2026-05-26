package com.volodymyrchikh.abitandstudhelp.repository;

import com.volodymyrchikh.abitandstudhelp.domain.SelectiveGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectiveGroupRepository extends JpaRepository<SelectiveGroup, Long> {

    @Query("SELECT sg FROM SelectiveGroup sg JOIN sg.specialties sp WHERE sp.id = :specialtyId AND sg.semester = :semester")
    List<SelectiveGroup> findBySpecialtyIdAndSemester(@Param("specialtyId") Long specialtyId, @Param("semester") Integer semester);
}
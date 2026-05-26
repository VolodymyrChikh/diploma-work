package com.volodymyrchikh.abitandstudhelp.repository;

import com.volodymyrchikh.abitandstudhelp.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByGroupName(String groupName);
    List<Lesson> findByGroupNameAndWeekTypeIn(String groupName, List<String> weekTypes);
    
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT l.groupName FROM Lesson l ORDER BY l.groupName")
    List<String> findDistinctGroupNames();
    
    void deleteBySourceFile(String sourceFile);
    void deleteAll();
}
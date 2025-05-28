package com.volodymyrchikh.abitandstudhelp.repository;

import com.volodymyrchikh.abitandstudhelp.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    @Query("""
                SELECT c FROM Comment c
                WHERE c.id IN (
                    SELECT MAX(c2.id) FROM Comment c2
                    GROUP BY c2.post.id
                )
            """)
    List<Comment> findLastCommentsForAllPosts();

    long countByPostId(Long postId);

}

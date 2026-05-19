package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.domain.Comment;
import com.volodymyrchikh.abitandstudhelp.dto.CommentRequest;
import com.volodymyrchikh.abitandstudhelp.dto.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    private final UserMapper userMapper;
    private final PostMapper postMapper;

    public CommentMapper(UserMapper userMapper, PostMapper postMapper) {
        this.userMapper = userMapper;
        this.postMapper = postMapper;
    }

    public CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .userResponse(userMapper.mapToResponse(comment.getUser()))
                .postResponse(postMapper.mapToResponse(comment.getPost()))
                .build();
    }
}

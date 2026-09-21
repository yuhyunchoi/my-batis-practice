package com.yuhyun.mybatispractice.comment.mapper;

import com.yuhyun.mybatispractice.comment.domain.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface CommentMapper {

    List<Comment> findCommentsByBoardId(Long boarId);

    Comment findById(Long commentId);

    int insert(Comment comment);

    int update(Comment comment);

    int deleteById(Long commentId);
}

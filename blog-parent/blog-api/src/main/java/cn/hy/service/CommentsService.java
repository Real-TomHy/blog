package cn.hy.service;

import cn.hy.vo.Result;
import cn.hy.vo.params.CommentParam;

public interface CommentsService {

    /**
     * 根据文章id查询所有评论
     * @param id
     * @return
     */
    Result commentsByArticleId(Long id);

    /**
     * 评论
     * @param commentParam
     * @return
     */
    Result comment(CommentParam commentParam);
}

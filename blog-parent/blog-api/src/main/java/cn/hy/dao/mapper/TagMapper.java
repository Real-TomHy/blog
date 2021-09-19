package cn.hy.dao.mapper;

import cn.hy.dao.pojo.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {
    /**
     * 根据文章id查询标签列表
     * @param articleId
     * @return
     */
    List<Tag> findTagsByArticleId(Long articleId);
    List<Long> findHotsTagIds(int limit);
    List<Tag> findTagsByTagIds(List<Long> tagIds);
}

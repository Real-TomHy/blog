package cn.hy.blog.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页需要
 * @param <T>
 */
@Data
public class PageResult<T> {

    private List<T> list;

    private Long total;
}

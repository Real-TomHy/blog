package cn.hy.blog.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * ๅ้กต้่ฆ
 * @param <T>
 */
@Data
public class PageResult<T> {

    private List<T> list;

    private Long total;
}

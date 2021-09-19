package cn.hy.service;

import cn.hy.vo.CategoryVo;
import cn.hy.vo.Result;

public interface CategoryService {

    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result findAllDetail();

    Result categoryDetailById(Long id);
}

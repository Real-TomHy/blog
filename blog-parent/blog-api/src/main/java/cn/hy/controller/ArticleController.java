package cn.hy.controller;

import cn.hy.common.aop.LogAnnotation;
import cn.hy.common.cache.Cache;
import cn.hy.service.ArticleService;
import cn.hy.vo.Result;
import cn.hy.vo.params.ArticleParam;
import cn.hy.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//json交互
@RestController
@RequestMapping("articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    /**
     * 首页文章列表
     * @param pageParams
     */
    @PostMapping
    //加上此注解 代表要对此接口记录日志
    @LogAnnotation(model="文章",operator="获取文章列表")
    public Result listArticle(@RequestBody PageParams pageParams){
//        int i=10/0;
        return articleService.listArticle(pageParams);
    }

    /**
     * 首页最热文章
     * @return
     */
    @Cache(expire=2 * 60 * 1000,name="hot_article")
    @PostMapping("hot")
    public Result hotArticle(){
        int limit=5;
        return articleService.hotArticle(limit);
    }

    /**
     * 最新文章
     * @return
     */
    @PostMapping("new")
    @Cache(expire=2 * 60 * 1000,name="new_article")
    public Result newArticles(){
        int limit=5;
        return articleService.newArticles(limit);
    }

    /**
     * 文章归档
     * @return
     */
    @PostMapping("listArchives")
    public Result listArchives(){
        return articleService.listArchives();
    }

    /**
     * 文章详情
     * @param articleId
     * @return
     */
    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id") Long articleId){
        return articleService.findArticleById(articleId);
    }

    /**
     * 发布文章
     * @param articleParam
     * @return
     */
    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        System.err.println(articleParam);
        return articleService.publish(articleParam);
    }
}

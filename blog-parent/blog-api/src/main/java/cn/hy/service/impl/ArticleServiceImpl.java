package cn.hy.service.impl;

import cn.hy.dao.dos.Archives;
import cn.hy.dao.mapper.ArticleBodyMapper;
import cn.hy.dao.mapper.ArticleMapper;
import cn.hy.dao.mapper.ArticleTagMapper;
import cn.hy.dao.pojo.Article;
import cn.hy.dao.pojo.ArticleBody;
import cn.hy.dao.pojo.ArticleTag;
import cn.hy.dao.pojo.SysUser;
import cn.hy.service.*;
import cn.hy.utils.UserThreadLocal;
import cn.hy.vo.ArticleBodyVo;
import cn.hy.vo.ArticleVo;
import cn.hy.vo.Result;
import cn.hy.vo.TagVo;
import cn.hy.vo.params.ArticleParam;
import cn.hy.vo.params.PageParams;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private CategoryService categoryService;
    @Autowired(required = false)
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired(required = false)
    private ArticleBodyMapper articleBodyMapper;
    @Autowired(required = false)
    private ArticleTagMapper articleTagMapper;

//    @Override
//    public  Result listArticle(PageParams pageParams) {
//        /**
//         * 1.分页查询 article数据库
//         */
//        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper();
////        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
//        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
//        if (pageParams.getCategoryId() != null){
//            // and category_id=#{categoryId}
//            queryWrapper.eq(Article::getCategoryId,pageParams.getCategoryId());
//        }
//        List<Long> articleIdList = new ArrayList<>();
//        if(pageParams.getTagId() !=null){
//            //加入标签 条件查询
//            //article表中 并没有tag字段 一篇文章 有多个标签
//            //article_tag  article_id 1 : n tag_id
//            LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
//            articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId,pageParams.getTagId());
//            List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagLambdaQueryWrapper);
//            for (ArticleTag articleTag : articleTags) {
//                articleIdList.add(articleTag.getArticleId());
//            }
//            if (articleIdList.size() > 0){
//                // and id in(1,2,3)
//                queryWrapper.in(Article::getId,articleIdList);
//            }
//        }

//        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
//        List<Article> records=articlePage.getRecords();
//        List<ArticleVo> articleVoList = copyList(records,true,false,true,false);
//        return Result.success(articleVoList);
//    }

    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        IPage<Article> articleIPage = articleMapper.listArticle(page, pageParams.getCategoryId(), pageParams.getTagId(), pageParams.getYear(), pageParams.getMonth());
        List<Article> records=articleIPage.getRecords();
        return Result.success(copyList(records,true,false,true,false));
    }

    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        //select id,title from article order by view_counts desc limit 5
        List<Article> articles=articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles,false,false,false,false));
    }

    @Override
    public Result newArticles(int limit) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        //select id,title from article order by view_counts desc limit 5
        List<Article> articles=articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles,false,false,false,false));
    }

    @Override
    public Result listArchives() {
        List<Archives> archivesList=articleMapper.listArchives();
        return Result.success(archivesList);
    }
    @Autowired
    private ThreadService threadService;
    @Override
    public Result findArticleById(Long articleId) {
        /**
         * 1. 根据id查询 文章信息
         * 2. 根据bodyId和categoryid 去做关联查询
         */

        Article article=this.articleMapper.selectById(articleId);
        ArticleVo articleVo=copy(article,true,true,true,true);
        //查看完文章了，新增阅读数，有没有问题呢？
        //查看完文章之后，本应该直接返回数据了，这时候做了一个更新操作，更新时加写锁，阻塞其他的读操作，性能就会比较低
        // 更新 增加了此次接口的 耗时 如果一旦更新出问题，不能影响 查看文章的操作
        //线程池  可以把更新操作 扔到线程池中去执行，和主线程就不相关了
        threadService.updateArticleViewCount(articleMapper,article);
        return Result.success(articleVo);
    }

    @Override
    public Result publish(ArticleParam articleParam) {
        //此接口要加入登录拦截器中
        SysUser sysUser= UserThreadLocal.get();
        /**
         * 1. 发布文章 目的构建Article对象
         * 2. 作者id 当前登录的用户
         * 3. 标签 要将标签加入管理列表中
         * 4. body 内容存储
         */
        Article article=new Article();
        article.setAuthorId(sysUser.getId());
        article.setWeight(Article.Article_Common);
        article.setViewCounts(0);
        article.setTitle(articleParam.getTitle());
        article.setSummary(articleParam.getSummary());
        article.setCommentCounts(0);
        article.setCreateDate(System.currentTimeMillis());
        article.setCategoryId(Long.valueOf(articleParam.getCategory().getId()));
        //插入之后 会生成一个文章id
        this.articleMapper.insert(article);
        //tag
        List<TagVo> tags = articleParam.getTags();
        if (tags != null){
            for (TagVo tag : tags) {
                Long articleId = article.getId();
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(Long.parseLong(tag.getId()));
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }
        }
        //body
        ArticleBody articleBody=new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);
        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);
        Map<String,String> map=new HashMap<>();
        map.put("id",article.getId().toString());
        return Result.success(map);
 }


     private List<ArticleVo> copyList(List<Article> records,boolean isAuthor,boolean isBody,boolean isTags,boolean isCategpry) {
         List<ArticleVo> articleVoList = new ArrayList<>();
         for (Article record : records) {
             articleVoList.add(copy(record,isTags,isAuthor,isBody,isCategpry));
         }
         return articleVoList;
     }

     private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody,boolean isCategory){
         ArticleVo articleVo = new ArticleVo();
         articleVo.setId(String.valueOf(article.getId()));
         BeanUtils.copyProperties(article,articleVo);

         articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));

         //并不是所有的接口 都需要标签 ，作者信息
         if (isTag){
             Long articleId = article.getId();
             articleVo.setTags(tagService.findTagsByArticleId(articleId));
         }
         if (isAuthor){
             Long authorId = article.getAuthorId();
             articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
         }
         if (isBody){
             Long bodyId = article.getBodyId();
             articleVo.setBody(findArticleBodyById(bodyId));
         }
         if (isCategory){
             Long categoryId = article.getCategoryId();
             articleVo.setCategory(categoryService.findCategoryById(categoryId));
         }
         return articleVo;
     }

     private ArticleBodyVo findArticleBodyById(Long bodyId) {
         ArticleBody articleBody =articleBodyMapper.selectById(bodyId);
         ArticleBodyVo articleBodyVo= new ArticleBodyVo();
         articleBodyVo.setContent(articleBody.getContent());
         return articleBodyVo;
     }

 }


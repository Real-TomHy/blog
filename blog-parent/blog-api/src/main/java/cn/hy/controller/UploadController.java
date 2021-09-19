package cn.hy.controller;

import cn.hy.utils.QiniuUtils;
import cn.hy.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("upload")
public class UploadController {
    @Autowired
    private QiniuUtils qiniuUtils;
    @PostMapping
    public Result upload(@RequestParam("image")MultipartFile file){
        //原始文件名称
        String originalFilename = file.getOriginalFilename();
        //唯一的文件名称
        String filename = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");
        //上传文件
        boolean upload=qiniuUtils.upload(file,filename);
        if(upload){
            return Result.success(QiniuUtils.url+filename);
        }
        return Result.fail(20001,"上传失败");
    }
}

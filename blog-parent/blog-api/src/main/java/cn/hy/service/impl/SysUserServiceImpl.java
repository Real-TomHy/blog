package cn.hy.service.impl;

import cn.hy.dao.mapper.SysUserMapper;
import cn.hy.dao.pojo.SysUser;
import cn.hy.service.LoginService;
import cn.hy.service.SysUserService;
import cn.hy.utils.JWTUtils;
import cn.hy.vo.ErrorCode;
import cn.hy.vo.LoginUserVo;
import cn.hy.vo.Result;
import cn.hy.vo.UserVo;
import cn.hy.vo.params.LoginParam;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private LoginService loginService;

    @Autowired(required = false)
    private SysUserMapper sysUserMapper;
    @Override
    public SysUser findUserById(Long authorId) {
        SysUser sysUser=(SysUser) sysUserMapper.selectById(authorId);
        if(null == sysUser){
            sysUser=new SysUser();
            sysUser.setNickname("腰缠万贯蟹老板");
        }
        return sysUser;
    }

    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.eq(SysUser::getPassword,password);
        queryWrapper.select(SysUser::getAccount,SysUser::getId,SysUser::getNickname,SysUser::getAvatar);
        queryWrapper.last("limit 1");
        return sysUserMapper.selectOne(queryWrapper);
    }



    @Override
    public Result findUserByToken(String token) {
        /**
         * 1. token合法性校验
         *    是否为空，解析是否成功 redis是否存在
         * 2. 如果校验失败 返回错误
         * 3. 如果成功，返回对应的结果 LoginUserVo
         */
        SysUser sysUser = loginService.checkToken(token);
        if (sysUser == null){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(String.valueOf(sysUser.getId()));
        loginUserVo.setNickname(sysUser.getNickname());
        loginUserVo.setAvatar(sysUser.getAvatar());
        loginUserVo.setAccount(sysUser.getAccount());
        return Result.success(loginUserVo);
    }

    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.last("limit 1");
        return this.sysUserMapper.selectOne(queryWrapper);
    }

    @Override
    public void save(SysUser sysUser) {
        this.sysUserMapper.insert(sysUser);
    }

    @Override
    public UserVo findUserVoById(Long authorId) {
        SysUser sysUser=(SysUser) sysUserMapper.selectById(authorId);
        if(null == sysUser){
            sysUser=new SysUser();
            sysUser.setId(1l);
            sysUser.setAvatar("/static/img/logo.b3a49c0.png");
            sysUser.setNickname("腰缠万贯蟹老板");
        }
        UserVo userVo= new UserVo();
        BeanUtils.copyProperties(sysUser,userVo);
        return userVo;
    }
}

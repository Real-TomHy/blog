package cn.hy.service;

import cn.hy.dao.pojo.SysUser;
import cn.hy.vo.Result;
import cn.hy.vo.UserVo;

public interface SysUserService  {
    SysUser findUserById(Long authorId);

    SysUser findUser(String account, String password);

    /**
     * 根据token查询用户信息
     * @param token
     * @return
     */
    Result findUserByToken(String token);

    /**
     * 根据账户查找用户
     * @param account
     * @return
     */
    SysUser findUserByAccount(String account);

    /**
     * 保存用户
     * @param sysUser
     */

    void save(SysUser sysUser);

    UserVo findUserVoById(Long authorId);
}

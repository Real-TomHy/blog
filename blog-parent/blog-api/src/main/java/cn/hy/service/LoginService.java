package cn.hy.service;

import cn.hy.dao.pojo.SysUser;
import cn.hy.vo.Result;
import cn.hy.vo.params.LoginParam;

public interface LoginService {

    /**
     * 登录功能
     * @param loginParam
     * @return
     */
    Result login(LoginParam loginParam);

    SysUser checkToken(String token);

    /**
     * 退出功能
     * @param token
     * @return
     */
    Result logout(String token);

    /**
     * 注册
     * @param loginParam
     * @return
     */
    Result register(LoginParam loginParam);
}

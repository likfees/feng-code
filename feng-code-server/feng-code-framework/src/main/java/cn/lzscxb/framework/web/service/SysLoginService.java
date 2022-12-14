package cn.lzscxb.framework.web.service;

import javax.annotation.Resource;

import cn.lzscxb.common.exception.user.CaptchaException;
import cn.lzscxb.common.exception.user.CaptchaExpireException;
import cn.lzscxb.common.exception.user.UserPasswordNotMatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import cn.lzscxb.domain.constant.CacheConstants;
import cn.lzscxb.domain.constant.Constants;
import cn.lzscxb.domain.entity.FengUsers;
import cn.lzscxb.domain.model.LoginUser;
import cn.lzscxb.common.core.redis.RedisCache;
import cn.lzscxb.common.exception.ServiceException;
import cn.lzscxb.common.utils.DateUtils;
import cn.lzscxb.common.utils.MessageUtils;
import cn.lzscxb.domain.utils.ServletUtils;
import cn.lzscxb.domain.utils.StringUtils;
import cn.lzscxb.common.utils.ip.IpUtils;
import cn.lzscxb.framework.manager.AsyncManager;
import cn.lzscxb.framework.manager.factory.AsyncFactory;
import cn.lzscxb.framework.security.context.AuthenticationContextHolder;
import cn.lzscxb.system.service.ISysConfigService;
import cn.lzscxb.system.service.IFengUsersService;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录校验方法
 * 
 * @author Likfees
 */
@Component
public class SysLoginService
{
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;
    
    @Autowired
    private IFengUsersService userService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 登录验证
     * 
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public Map<String, Object> login(String username, String password, String code, String uuid)
    {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        // 验证码开关
        if (captchaEnabled)
        {
            validateCaptcha(username, code, uuid);
        }
        // 用户验证
        Authentication authentication = null;
        try
        {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }
        finally
        {
            AuthenticationContextHolder.clearContext();
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        recordLoginInfo(loginUser.getUserId());

        Map<String, Object> result = new HashMap<>();
        // 生成token
        result.put("token", tokenService.createToken(loginUser));
        result.put("roles", loginUser.getUser().getRoles());
        return result;
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
            throw new CaptchaException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId)
    {
        FengUsers fengUsers = new FengUsers();
        fengUsers.setUserId(userId);
        fengUsers.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        fengUsers.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(fengUsers);
    }
}

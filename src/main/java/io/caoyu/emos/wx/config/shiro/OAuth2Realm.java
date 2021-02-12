package io.caoyu.emos.wx.config.shiro;

import io.caoyu.emos.wx.db.pojo.TbUser;
import io.caoyu.emos.wx.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class OAuth2Realm extends AuthorizingRealm {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        TbUser user = (TbUser) principals.getPrimaryPrincipal();
        int userId = user.getId();

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //TODO 查询用户的权限列表
        //用户权限列表
        Set<String> permsSet = userService.searchUserPermissions(userId);
        //TODO 把权限列表添加到info对象中
        info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String accessToken = (String) token.getPrincipal();
        int userId = jwtUtil.getUserId(accessToken);
        //查询用户信息
        TbUser user = userService.searchById(userId);

        if (user == null) {
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, accessToken, getName());
        return info;
    }
}

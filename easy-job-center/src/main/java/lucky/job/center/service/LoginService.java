package lucky.job.center.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lucky.job.center.entity.EasyJobUser;
import lucky.job.center.util.CookieUtil;
import lucky.job.center.util.JacksonUtil;
import lucky.job.core.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

/**
 * @author: luckylau
 * @Date: 2020/12/10 20:43
 * @Description:
 */
@Service
public class LoginService {

    public static final String LOGIN_IDENTITY_KEY = "EASY_JOB_LOGIN_IDENTITY";

    @Autowired
    private IEasyJobUserService easyJobUserService;


    private String makeToken(EasyJobUser easyJobUser) {
        String tokenJson = JacksonUtil.writeValueAsString(easyJobUser);
        if (tokenJson == null) {
            return null;
        }
        return new BigInteger(tokenJson.getBytes()).toString(16);
    }

    private EasyJobUser parseToken(String tokenHex) {
        EasyJobUser easyJobUser = null;
        if (tokenHex != null) {
            // username_password(md5)
            String tokenJson = new String(new BigInteger(tokenHex, 16).toByteArray());
            easyJobUser = JacksonUtil.readValue(tokenJson, EasyJobUser.class);
        }
        return easyJobUser;
    }


    public ReturnT<String> login(HttpServletRequest request, HttpServletResponse response, String username, String password, boolean ifRemember) {

        // param
        if (username == null || username.trim().length() == 0 || password == null || password.trim().length() == 0) {
            return new ReturnT<>(500, "login_param_empty");
        }

        // valid passowrd
        EasyJobUser xxlJobUser = easyJobUserService.getOne(new QueryWrapper<EasyJobUser>().eq("username", username));
        if (xxlJobUser == null) {
            return new ReturnT<>(500, "login_param_unvalid");
        }
        String passwordMd5 = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!passwordMd5.equals(xxlJobUser.getPassword())) {
            return new ReturnT<>(500, "login_param_unvalid");
        }

        String loginToken = makeToken(xxlJobUser);

        // do login
        CookieUtil.set(response, LOGIN_IDENTITY_KEY, loginToken, ifRemember);
        return ReturnT.SUCCESS;
    }

    /**
     * logout
     *
     * @param request
     * @param response
     */
    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
        return ReturnT.SUCCESS;
    }

    /**
     * logout
     *
     * @param request
     * @return
     */
    public EasyJobUser ifLogin(HttpServletRequest request, HttpServletResponse response) {
        String cookieToken = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
        if (cookieToken != null) {
            EasyJobUser cookieUser = null;
            try {
                cookieUser = parseToken(cookieToken);
            } catch (Exception e) {
                logout(request, response);
            }
            if (cookieUser != null) {
                EasyJobUser dbUser = easyJobUserService.getOne(new QueryWrapper<EasyJobUser>().eq("username", cookieUser.getUsername()));
                if (dbUser != null) {
                    if (cookieUser.getPassword().equals(dbUser.getPassword())) {
                        return dbUser;
                    }
                }
            }
        }
        return null;
    }
}

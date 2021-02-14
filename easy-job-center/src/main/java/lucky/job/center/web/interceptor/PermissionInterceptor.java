package lucky.job.center.web.interceptor;

import lucky.job.center.entity.EasyJobUser;
import lucky.job.center.service.LoginService;
import lucky.job.center.web.annotation.PermissionLimit;
import lucky.job.core.exception.EasyJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: luckylau
 * @Date: 2020/12/10 20:42
 * @Description:
 */
@Component
public class PermissionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }

        // if need login
        boolean needLogin = true;
        boolean needAdminuser = false;
        HandlerMethod method = (HandlerMethod) handler;
        PermissionLimit permission = method.getMethodAnnotation(PermissionLimit.class);
        if (permission != null) {
            needLogin = permission.limit();
            needAdminuser = permission.admin();
        }

        if (needLogin) {
            EasyJobUser loginUser = loginService.ifLogin(request, response);
            if (loginUser == null) {
                response.sendRedirect(request.getContextPath() + "/toLogin");
                return false;
            }
            if (needAdminuser && loginUser.getRole() != 1) {
                throw new EasyJobException("system_permission_limit");
            }
            request.setAttribute(LoginService.LOGIN_IDENTITY_KEY, loginUser);
        }

        return super.preHandle(request, response, handler);
    }
}

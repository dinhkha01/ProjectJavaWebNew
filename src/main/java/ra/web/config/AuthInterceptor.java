package ra.web.config;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object userLogin = request.getSession().getAttribute("userLogin");
        String requestURI = request.getRequestURI();

        // Kiểm tra đường dẫn admin
        if (requestURI.startsWith(request.getContextPath() + "/admin/")) {
            if (userLogin == null || !"ADMIN".equals(userLogin)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }

        // Kiểm tra đường dẫn candidate
        if (requestURI.startsWith(request.getContextPath() + "/candidate/")) {
            if (userLogin == null || !"CANDIDATE".equals(userLogin)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }

        return true;
    }
}
package ynu.edu.pims.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ynu.edu.pims.common.UserContext;
import ynu.edu.pims.redis.RedisService;
import ynu.edu.pims.utils.JwtUtil;

import java.util.Map;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 预检请求直接放行，交给 CORS 处理
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }

        String token = authorizationHeader.replace("Bearer ", "");
        if (token == null) {
            response.setStatus(401);
            return false;
        }

        // 令牌是否在黑名单
        String tokenId = jwtUtil.getTokenId(token);
        if (tokenId == null || redisService.isTokenInBlackList(tokenId)) {
            response.setStatus(401);
            return false;
        }

        // 验证令牌
        if (token != null) {
            Map<String, String> userInfo = jwtUtil.verifyToken(token);
            if (userInfo != null) {
                UserContext.setUser(userInfo);
                return true;
            }
        }

        response.setStatus(401);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        UserContext.remove();
    }
}
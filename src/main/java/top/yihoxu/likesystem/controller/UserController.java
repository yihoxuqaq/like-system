package top.yihoxu.likesystem.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.yihoxu.likesystem.common.BaseResponse;
import top.yihoxu.likesystem.common.ResultUtils;
import top.yihoxu.likesystem.constant.UserConstant;
import top.yihoxu.likesystem.model.entity.User;
import top.yihoxu.likesystem.service.UserService;

/**
 * @author yihoxu
 * @date 2025/4/23  23:42
 * @description 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/login")
    public BaseResponse<User> login(Long userId, HttpServletRequest request) {
        User user = userService.getById(userId);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return ResultUtils.success(user);
    }
    @GetMapping("/get/login")
    public BaseResponse<User> getLoginUser(HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        return ResultUtils.success(loginUser);
    }

}

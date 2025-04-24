package top.yihoxu.likesystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import top.yihoxu.likesystem.constant.UserConstant;
import top.yihoxu.likesystem.mapper.UserMapper;
import top.yihoxu.likesystem.model.entity.User;
import top.yihoxu.likesystem.service.UserService;

import org.springframework.stereotype.Service;

/**
 * @author hushi
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2025-04-23 23:45:21
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {


    @Override
    public User getLoginUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        return user;
    }
}





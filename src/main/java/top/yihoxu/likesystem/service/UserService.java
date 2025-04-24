package top.yihoxu.likesystem.service;


import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import top.yihoxu.likesystem.model.entity.User;

/**
* @author hushi
* @description 针对表【user】的数据库操作Service
* @createDate 2025-04-23 23:45:21
*/
public interface UserService extends IService<User> {

    User getLoginUser(HttpServletRequest request);
}

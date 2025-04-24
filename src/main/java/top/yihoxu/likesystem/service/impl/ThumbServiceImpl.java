package top.yihoxu.likesystem.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import top.yihoxu.likesystem.exception.BusinessException;
import top.yihoxu.likesystem.exception.ErrorCode;
import top.yihoxu.likesystem.mapper.ThumbMapper;
import top.yihoxu.likesystem.model.dto.thumb.DoThumbRequest;
import top.yihoxu.likesystem.model.entity.Blog;
import top.yihoxu.likesystem.model.entity.Thumb;
import top.yihoxu.likesystem.model.entity.User;
import top.yihoxu.likesystem.service.BlogService;
import top.yihoxu.likesystem.service.ThumbService;
import top.yihoxu.likesystem.service.UserService;

/**
 * @author hushi
 * @description 针对表【thumb】的数据库操作Service实现
 * @createDate 2025-04-23 23:45:15
 */
@Service
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb>
        implements ThumbService {

    @Resource
    private UserService userService;
    @Resource
    private BlogService blogService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        User loginUser = userService.getLoginUser(request);

        synchronized (loginUser.getId().toString().intern()) {
            return transactionTemplate.execute(status -> {
                boolean exists = this.lambdaQuery()
                        .eq(Thumb::getUserId, loginUser.getId())
                        .eq(Thumb::getBlogId, doThumbRequest.getBlogId())
                        .exists();
                if (exists) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "已经点赞过了");
                }
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId, doThumbRequest.getBlogId())
                        .setSql("thumbCount = thumbCount + 1")
                        .update();

                Thumb thumb = new Thumb();
                thumb.setUserId(loginUser.getId());
                thumb.setBlogId(doThumbRequest.getBlogId());
                boolean save = this.save(thumb);
                return save && update;

            });
        }
    }

    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {

        if (doThumbRequest == null || doThumbRequest.getBlogId() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        synchronized (loginUser.getId().toString().intern()) {
            Long blogId = doThumbRequest.getBlogId();
            return transactionTemplate.execute(status -> {
                Thumb thumb = this.lambdaQuery()
                        .eq(Thumb::getBlogId, blogId)
                        .eq(Thumb::getUserId, loginUser.getId())
                        .one();
                if (thumb == null) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "blog没有被你点赞过");
                }
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId, blogId)
                        .setSql("thumbCount = thumbCount - 1")
                        .update();
                return update && this.removeById(thumb.getId());
            });
        }

    }
}





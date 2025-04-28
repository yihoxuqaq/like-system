package top.yihoxu.likesystem.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import top.yihoxu.likesystem.constant.ThumbConstant;
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
    private RedisTemplate<String, Object> redisTemplate;

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
                Long blogId = doThumbRequest.getBlogId();
                //对用户是否点赞从mysql查询 换成redis查询
               /* boolean exists = this.lambdaQuery()
                        .eq(Thumb::getUserId, loginUser.getId())
                        .eq(Thumb::getBlogId, doThumbRequest.getBlogId())
                        .exists();*/
                //从redis查询
                Boolean exists = this.hasThumb(blogId, loginUser.getId());
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
                //点赞记录存入redis
                if (save) {
                    redisTemplate.opsForHash().put(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId().toString(), blogId.toString(), thumb.getId());
                }
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
                //对用户是否点赞从mysql查询 换成redis查询
               /* Thumb thumb = this.lambdaQuery()
                        .eq(Thumb::getBlogId, blogId)
                        .eq(Thumb::getUserId, loginUser.getId())
                        .one();*/
                //从redis中查询
                Long thumbId = (Long) redisTemplate.opsForHash().get(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId().toString(), blogId.toString());
                if (thumbId == null) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "blog没有被你点赞过");
                }
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId, blogId)
                        .setSql("thumbCount = thumbCount - 1")
                        .update();
                boolean success = update && this.removeById(thumbId);
                //删除redis中点赞记录
                if (success) {
                    redisTemplate.opsForHash().delete(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId().toString(), blogId.toString());
                }
                return success;
            });
        }

    }


    @Override
    public Boolean hasThumb(Long blogId, Long userId) {
        return redisTemplate.opsForHash().hasKey(ThumbConstant.USER_THUMB_KEY_PREFIX + userId, blogId.toString());
    }

}





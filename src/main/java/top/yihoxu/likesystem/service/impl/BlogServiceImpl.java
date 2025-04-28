package top.yihoxu.likesystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import top.yihoxu.likesystem.constant.ThumbConstant;
import top.yihoxu.likesystem.model.entity.Blog;
import top.yihoxu.likesystem.model.entity.Thumb;
import top.yihoxu.likesystem.model.entity.User;
import top.yihoxu.likesystem.model.vo.BlogVO;
import top.yihoxu.likesystem.service.BlogService;
import top.yihoxu.likesystem.mapper.BlogMapper;
import org.springframework.stereotype.Service;
import top.yihoxu.likesystem.service.ThumbService;
import top.yihoxu.likesystem.service.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author hushi
 * @description 针对表【blog】的数据库操作Service实现
 * @createDate 2025-04-23 23:32:18
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
        implements BlogService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private ThumbService thumbService;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public BlogVO getBlogVOById(long blogId, HttpServletRequest request) {
        Blog blog = this.getById(blogId);
        User loginUser = userService.getLoginUser(request);
        return this.getBlogVO(blog, loginUser);
    }

    @Override
    public List<BlogVO> getBlogVOList(List<Blog> blogList, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        HashMap<Long, Boolean> blogIdHasThumbMap = new HashMap<>();
        if (ObjUtil.isNotEmpty(loginUser)) {
            //获取全部blogId
            List<String> blogId = blogList.stream()
                    .map(i -> {
                        return i.getId().toString();
                    })
                    .collect(Collectors.toList());
            //根据登录的用户查看点赞过哪些blog
            /*List<Thumb> thumbList = thumbService.lambdaQuery()
                    .eq(Thumb::getUserId, loginUser.getId())
                    .in(Thumb::getBlogId, blogSet)
                    .list();*/
            //从redis中查询
            List<Long> thumbId = redisTemplate.opsForHash().multiGet(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId().toString(), blogId);
            for (int i = 0; i < thumbId.size(); i++) {
                if (thumbId.get(i) == null) {
                    continue;
                }
                blogIdHasThumbMap.put(Long.valueOf(blogId.get(i)), true);
            }
        }
        return blogList.stream()
                .map(blog -> {
                    BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
                    blogVO.setHasThumb(blogIdHasThumbMap.get(blog.getId()));
                    return blogVO;
                }).collect(Collectors.toList());
    }

    private BlogVO getBlogVO(Blog blog, User loginUser) {
        BlogVO blogVO = new BlogVO();
        BeanUtil.copyProperties(blog, blogVO);

        if (loginUser == null) {
            return blogVO;
        }

        Thumb thumb = thumbService.lambdaQuery()
                .eq(Thumb::getUserId, loginUser.getId())
                .eq(Thumb::getBlogId, blog.getId())
                .one();
        blogVO.setHasThumb(thumb != null);

        return blogVO;
    }

}





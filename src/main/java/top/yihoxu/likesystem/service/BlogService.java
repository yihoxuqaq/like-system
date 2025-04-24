package top.yihoxu.likesystem.service;

import jakarta.servlet.http.HttpServletRequest;
import top.yihoxu.likesystem.model.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;
import top.yihoxu.likesystem.model.vo.BlogVO;

import java.util.List;

/**
* @author hushi
* @description 针对表【blog】的数据库操作Service
* @createDate 2025-04-23 23:32:18
*/
public interface BlogService extends IService<Blog> {

    BlogVO getBlogVOById(long blogId, HttpServletRequest request);

    List<BlogVO> getBlogVOList(List<Blog> blogList, HttpServletRequest request);

}

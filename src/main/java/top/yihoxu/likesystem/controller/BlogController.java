package top.yihoxu.likesystem.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yihoxu.likesystem.common.BaseResponse;
import top.yihoxu.likesystem.common.ResultUtils;
import top.yihoxu.likesystem.model.entity.Blog;
import top.yihoxu.likesystem.model.vo.BlogVO;
import top.yihoxu.likesystem.service.BlogService;

import java.util.List;

/**
 * @author yihoxu
 * @date 2025/4/24  00:58
 * @description 博客接口
 */
@RestController
@RequestMapping("blog")
public class BlogController {
    @Resource
    private BlogService blogService;

    @GetMapping("/get")
    public BaseResponse<BlogVO> get(long blogId, HttpServletRequest request) {
        BlogVO blogVO = blogService.getBlogVOById(blogId, request);
        return ResultUtils.success(blogVO);
    }

    @GetMapping("/list")
    public BaseResponse<List<BlogVO>> list(HttpServletRequest request) {
        List<Blog> blogList = blogService.list();
        List<BlogVO> blogVOList = blogService.getBlogVOList(blogList, request);
        return ResultUtils.success(blogVOList);
    }



}

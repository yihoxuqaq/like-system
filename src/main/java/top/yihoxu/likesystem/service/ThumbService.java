package top.yihoxu.likesystem.service;


import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import top.yihoxu.likesystem.model.dto.thumb.DoThumbRequest;
import top.yihoxu.likesystem.model.entity.Thumb;

/**
* @author hushi
* @description 针对表【thumb】的数据库操作Service
* @createDate 2025-04-23 23:45:15
*/
public interface ThumbService extends IService<Thumb> {
    /**
     * 点赞
     * @param doThumbRequest
     * @param request
     * @return {@link Boolean }
     */
    Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);


    /**
     * 取消点赞
     * @param doThumbRequest
     * @param request
     * @return {@link Boolean }
     */
    Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);

    /**
     * 是否已点赞
     * @param blogId
     * @param userId
     * @return
     */
    Boolean hasThumb(Long blogId, Long userId);



}

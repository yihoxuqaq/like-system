package top.yihoxu.likesystem.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yihoxu.likesystem.common.BaseResponse;
import top.yihoxu.likesystem.common.ResultUtils;
import top.yihoxu.likesystem.model.dto.thumb.DoThumbRequest;
import top.yihoxu.likesystem.service.ThumbService;

/**
 * @author yihoxu
 * @date 2025/4/24  19:17
 * @description 点赞接口
 */

@RestController
@RequestMapping("/thumb")
public class ThumbController {
    @Resource
    private ThumbService thumbService;

    @PostMapping("/do")
    public BaseResponse<Boolean> doThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request) {
        Boolean success = thumbService.doThumb(doThumbRequest, request);
        return ResultUtils.success(success);
    }

    @PostMapping("/undo")
    public BaseResponse<Boolean> undoThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request) {
        Boolean success = thumbService.undoThumb(doThumbRequest, request);
        return ResultUtils.success(success);
    }

}


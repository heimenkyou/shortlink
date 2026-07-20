package cn.luowb.shortlink.project.handler;

import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.project.dto.req.LinkCreateReqDTO;
import cn.luowb.shortlink.project.dto.resp.LinkCreateRespDTO;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * 自定义流控策略
 */
public class CustomBlockHandler {

    public static Result<LinkCreateRespDTO> createShortLinkBlockHandlerMethod(LinkCreateReqDTO requestParam, BlockException exception) {
        return new Result<LinkCreateRespDTO>().setCode("B100000").setMessage("当前访问网站人数过多，请稍后再试...");
    }
}
package cn.luowb.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import cn.luowb.shortlink.admin.remote.dto.req.LinkCreateReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.LinkPageReqDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.LinkCreateRespDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.dto.PageResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import java.util.HashMap;
import java.util.Map;

/**
 * 短链接平台远程调用服务
 */
public interface LinkRemoteService {

    /**
     * 创建短链接
     */
    default Result<LinkCreateRespDTO> createShortLink(LinkCreateReqDTO requestParam) {
        String resultPageJson = HttpUtil.post("http://localhost:8001/api/short-link/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultPageJson, new TypeReference<>() {
        });
    }

    /**
     * 分页查询短链接
     */
    default Result<PageResult<LinkPageRespDTO>> pageShortLink(LinkPageReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultPageJson = HttpUtil.get("http://localhost:8001/api/short-link/v1/page", requestMap);
        return JSON.parseObject(resultPageJson, new TypeReference<>() {
        });
    }
}

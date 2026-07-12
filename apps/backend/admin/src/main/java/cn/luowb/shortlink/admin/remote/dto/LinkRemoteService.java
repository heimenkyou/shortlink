package cn.luowb.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import cn.luowb.shortlink.admin.remote.dto.req.LinkCreateReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.LinkPageReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.LinkUpdateReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.TrashSaveReqDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.GroupCountQueryRespDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.LinkCreateRespDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.WebsiteMetadataRespDTO;
import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.convention.result.Results;
import cn.luowb.shortlink.common.dto.PageResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import java.util.HashMap;
import java.util.List;
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


    /**
     * 查询短链接分组下的短链接数量
     */
    default Result<List<GroupCountQueryRespDTO>> groupShortLinkCount(List<String> gidList) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gidList", gidList);
        String resultPageJson = HttpUtil.get("http://localhost:8001/api/short-link/admin/v1/group/count", requestMap);
        return JSON.parseObject(resultPageJson, new TypeReference<>() {
        });
    }

    default Result<Void> updateShortLink(LinkUpdateReqDTO requestParam) {
        HttpUtil.post("http://localhost:8001/api/short-link/v1/update", JSON.toJSONString(requestParam));
        return Results.success(null);
    }

    default Result<WebsiteMetadataRespDTO> fetchMetadata(String url) {
        String resultPageJson = HttpUtil.get("http://localhost:8001/api/short-link/v1/metadata", Map.of("url", url));
        return JSON.parseObject(resultPageJson, new TypeReference<>() {
        });
    }

    /**
     * 将链接移动到回收站
     *
     * @param requestParam 将链接移动到回收站请求参数
     */
    default Result<Void> saveTrash(TrashSaveReqDTO requestParam) {
        String resultPageJson = HttpUtil.post("http://localhost:8001/api/short-link/v1/trash/save", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultPageJson, new TypeReference<>() {
        });
    }
}

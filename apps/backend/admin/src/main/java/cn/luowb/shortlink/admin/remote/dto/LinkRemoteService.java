package cn.luowb.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import cn.luowb.shortlink.admin.remote.dto.req.*;
import cn.luowb.shortlink.admin.remote.dto.resp.GroupCountQueryRespDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.LinkCreateRespDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.WebsiteMetadataRespDTO;
import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.convention.result.Results;
import cn.luowb.shortlink.common.dto.PageResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import jakarta.validation.Valid;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

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

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页查询短链接请求参数
     * @return 分页查询短链接响应
     */
    default Result<PageResult<LinkPageRespDTO>> pageTrashLink(@Valid @MonotonicNonNull TrashLinkPageReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        requestMap.put("gidList", requestParam.getGidList());
        String resultPageJson = HttpUtil.get("http://localhost:8001/api/short-link/v1/trash/page", requestMap);
        return JSON.parseObject(resultPageJson, new TypeReference<>() {
        });
    }

    /**
     * 从回收站中恢复链接
     *
     * @param requestParam 从回收站中恢复链接请求参数
     * @return 从回收站中恢复链接响应
     */
    default Result<Void> recoverTrash(TrashRecoverReqDTO requestParam) {
        String resultPageJson = HttpUtil.post("http://localhost:8001/api/short-link/v1/trash/recover", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultPageJson, new TypeReference<>() {
        });
    }
}

package cn.luowb.shortlink.admin.remote.dto;

import cn.luowb.shortlink.admin.remote.dto.req.*;
import cn.luowb.shortlink.admin.remote.dto.resp.*;
import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.dto.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 短链接平台远程调用服务
 */
@FeignClient(name = "${shortlink.project.service-name:shortlink-project}")
public interface LinkRemoteService {

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/v1/create")
    Result<LinkCreateRespDTO> createShortLink(@RequestBody LinkCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/v1/page")
    Result<PageResult<LinkPageRespDTO>> pageShortLink(@SpringQueryMap LinkPageReqDTO requestParam);


    /**
     * 查询短链接分组下的短链接数量
     */
    @GetMapping("/api/short-link/v1/group/count")
    Result<List<GroupCountQueryRespDTO>> groupShortLinkCount(@RequestParam("gidList") List<String> gidList);

    @PostMapping("/api/short-link/v1/update")
    Result<Void> updateShortLink(@RequestBody LinkUpdateReqDTO requestParam);

    @GetMapping("/api/short-link/v1/metadata")
    Result<WebsiteMetadataRespDTO> fetchMetadata(@RequestParam("url") String url);

    /**
     * 查询单个短链接监控数据
     */
    @GetMapping("/api/short-link/v1/stats")
    Result<ShortLinkStatsRespDTO> shortLinkStats(@SpringQueryMap ShortLinkStatsReqDTO requestParam);

    /**
     * 查询分组短链接监控数据
     */
    @GetMapping("/api/short-link/v1/stats/group")
    Result<ShortLinkStatsRespDTO> groupShortLinkStats(@SpringQueryMap ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 查询单个短链接访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record")
    Result<PageResult<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(
            @SpringQueryMap ShortLinkStatsAccessRecordReqDTO requestParam);

    /**
     * 查询分组短链接访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record/group")
    Result<PageResult<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(
            @SpringQueryMap ShortLinkGroupStatsAccessRecordReqDTO requestParam);

    /**
     * 将链接移动到回收站
     *
     * @param requestParam 将链接移动到回收站请求参数
     */
    @PostMapping("/api/short-link/v1/trash/save")
    Result<Void> saveTrash(@RequestBody TrashSaveReqDTO requestParam);

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页查询短链接请求参数
     * @return 分页查询短链接响应
     */
    @GetMapping("/api/short-link/v1/trash/page")
    Result<PageResult<LinkPageRespDTO>> pageTrashLink(@SpringQueryMap TrashLinkPageReqDTO requestParam);

    /**
     * 从回收站中恢复链接
     *
     * @param requestParam 从回收站中恢复链接请求参数
     * @return 从回收站中恢复链接响应
     */
    @PostMapping("/api/short-link/v1/trash/recover")
    Result<Void> recoverTrash(@RequestBody TrashRecoverReqDTO requestParam);

    /**
     * 从回收站删除链接
     *
     * @param requestParam 从回收站删除链接请求参数
     */
    @PostMapping("/api/short-link/v1/trash/delete")
    Result<Void> deleteTrash(@RequestBody TrashDeleteReqDTO requestParam);
}

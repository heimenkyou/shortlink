package cn.luowb.shortlink.project.service;

import cn.luowb.shortlink.common.dto.PageResult;
import cn.luowb.shortlink.project.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import cn.luowb.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import cn.luowb.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import cn.luowb.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import cn.luowb.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import cn.luowb.shortlink.project.dto.resp.ShortLinkStatsRespDTO;

/**
 * 短链接监控接口层
 */
public interface ShortLinkStatsService {

    /**
     * 获取单个短链接监控数据
     *
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);

    /**
     * 获取分组短链接监控数据
     *
     * @param requestParam 获取分组短链接监控数据入参
     * @return 分组短链接监控数据
     */
    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     *
     * @param requestParam 获取短链接监控访问记录数据入参
     * @return 访问记录监控数据
     */
    PageResult<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     *
     * @param requestParam 获取分组短链接监控访问记录数据入参
     * @return 分组访问记录监控数据
     */
    PageResult<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}

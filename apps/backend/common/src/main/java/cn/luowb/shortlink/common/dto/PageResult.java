package cn.luowb.shortlink.common.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "通用分页响应数据")
public class PageResult<T> {
    @Schema(description = "当前页码")
    private Long current;

    @Schema(description = "每页大小")
    private Long size;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "当前页数据")
    private List<T> records;

    /**
     * 将 MyBatis-Plus 的 IPage 转换为干净的 PageResult
     */
    public static <T> PageResult<T> of(IPage<T> iPage) {
        return new PageResult<>(
                iPage.getCurrent(),
                iPage.getSize(),
                iPage.getTotal(),
                iPage.getRecords()
        );
    }
}
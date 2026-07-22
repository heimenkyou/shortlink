package cn.luowb.shortlink.project.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NegativeOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NegativeOrZero
public class WebsiteMetadataRespDTO {
    /**
     * 网站标题
     */
    @Schema(description = "网站标题")
    private String title;

    /**
     * 网站图标URL
     */
    @Schema(description = "网站图标URL")
    private String favicon;
}

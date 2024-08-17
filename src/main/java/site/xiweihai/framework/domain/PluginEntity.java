package site.xiweihai.framework.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PluginEntity {
    private Long pluginId;
    private String pluginName;
    private String pluginDesc;
    private String pluginPath;
    private Integer pluginStatus;
    private LocalDateTime updateTime;
}

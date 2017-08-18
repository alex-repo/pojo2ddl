package com.ap.database.utils.model.scheduled;

import java.util.ArrayList;
import java.util.List;

/**
 * Запланированный тех. перерыв, имеет некоторое расписание в соотвествии с которым выставляются действующие тех. перерывы.
 */
public class ScheduledBreak {

    /** Идентификатор */
    private Long id;
    /** Идентификаторы блоков в которых устанавливаестя ТП */
    private List<Long> blockIds;
    /** Идентификатор группы сервисов */
    private String groupId;
    /** Расписание */
    private String cron;
    /** Признак включен\выключен  */
    private boolean enabled;
    /** Признак удаления */
    private boolean deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getBlockIds() {
        if (blockIds == null) {
            blockIds = new ArrayList<>();
        }
        return blockIds;
    }

    public void setBlockIds(List<Long> blockIds) {
        this.blockIds = blockIds;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}

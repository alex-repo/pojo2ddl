package com.ap.database.utils.model.service;

/**
 * Группа сервисов
 */
public class ServiceGroup {

    /** Идентификатор */
    private Long id;
    /** Намиенование группы */
    private String name;
    /** Признак возможности установки ТП */
    private boolean autoBreakEnabled;
    /** Признак удаления */
    private boolean deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAutoBreakEnabled() {
        return autoBreakEnabled;
    }

    public void setAutoBreakEnabled(boolean autoBreakEnabled) {
        this.autoBreakEnabled = autoBreakEnabled;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}

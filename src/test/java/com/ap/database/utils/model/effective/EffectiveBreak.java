package com.ap.database.utils.model.effective;

import java.util.Date;

/**
 * Действующий ТП, установленный на конкретный интервал времени.
 */
public class EffectiveBreak {

    /** Идентификатор */
    private Long id;
    /** Идентификатор блока */
    private Long blockId;
    /** Тип ТП */
    private BreakType type;
    /** Идентификатор планового ТП инициировавшего текущий действующий ТП */
    private Long sheduledBreakId;
    /** Идентификатор сервиса инициировавшего текущий действующий ТП */
    private Long initialServiceId;
    /** Начало действия тех перерыва */
    private Date fromDatetime;
    /** Окончание действия тех перерыва */
    private Date toDatetime;
    /** Признак активности ТП (снят\неснят) */
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBlockId() {
        return blockId;
    }

    public void setBlockId(Long blockId) {
        this.blockId = blockId;
    }

    public Long getInitialServiceId() {
        return initialServiceId;
    }

    public void setInitialServiceId(Long initialServiceId) {
        this.initialServiceId = initialServiceId;
    }

    public BreakType getType() {
        return type;
    }

    public void setType(BreakType type) {
        this.type = type;
    }

    public Date getFromDatetime() {
        return fromDatetime;
    }

    public void setFromDatetime(Date fromDatetime) {
        this.fromDatetime = fromDatetime;
    }

    public Date getToDatetime() {
        return toDatetime;
    }

    public void setToDatetime(Date toDatetime) {
        this.toDatetime = toDatetime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getSheduledBreakId() {
        return sheduledBreakId;
    }

    public void setSheduledBreakId(Long sheduledBreakId) {
        this.sheduledBreakId = sheduledBreakId;
    }
}

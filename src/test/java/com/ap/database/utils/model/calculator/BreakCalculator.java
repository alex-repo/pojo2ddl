package com.ap.database.utils.model.calculator;

/**
 * Парамтеры включения автоматического ТП
 */
public class BreakCalculator {

    /** Идентификатор сервиса */
    private Long serviceId;
    /** Кол-во ошибок (порог) */
    private Long errorsThreshold;
    /** Период расчета в ms */
    private Long intervalInMillis;
    /** See {@link BreakStrategy} */
    private BreakStrategy strategy;
    /** Признак контроля таймаутов */
    private boolean checkTimeout;
    /** Таймаут в ms */
    private Long timeoutInMillis;
    /** Признак удаления */
    private boolean deleted;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getErrorsThreshold() {
        return errorsThreshold;
    }

    public void setErrorsThreshold(Long errorsThreshold) {
        this.errorsThreshold = errorsThreshold;
    }

    public Long getIntervalInMillis() {
        return intervalInMillis;
    }

    public void setIntervalInMillis(Long intervalInMillis) {
        this.intervalInMillis = intervalInMillis;
    }

    public BreakStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(BreakStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean isCheckTimeout() {
        return checkTimeout;
    }

    public void setCheckTimeout(boolean checkTimeout) {
        this.checkTimeout = checkTimeout;
    }

    public Long getTimeoutInMillis() {
        return timeoutInMillis;
    }

    public void setTimeoutInMillis(Long timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}

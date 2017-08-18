package com.ap.database.utils.model.effective;

/**
 * Вид ТП
 */
public enum BreakType {

    /** Автоматический, выставлен в результате накопления ошибок */
    AUTOMATIC,
    /** Вручную установленный администратором */
    MANUAL,
    /** Выставленный планировщиком по расписанию */
    SHEDULED
}

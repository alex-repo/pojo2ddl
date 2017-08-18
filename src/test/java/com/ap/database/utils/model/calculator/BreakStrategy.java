package com.ap.database.utils.model.calculator;

/**
 * Стратегия расчета автоматического ТП
 */
public enum BreakStrategy {

    /** По кол-ву ошибок в единицу времени */
    ERRORS_COUNT,
    /** По соотношению ошибок к успешным вызовам в единицу времени */
    ERRORS_RATIO
}

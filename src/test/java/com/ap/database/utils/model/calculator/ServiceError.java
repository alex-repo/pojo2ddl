package com.ap.database.utils.model.calculator;

/**
 * Код ошибки
 */
public class ServiceError {

    /** Идентификатор */
    private Long id;
    /** Код ошибки */
    private String code;
    /** Сообщение об ошибке */
    private String errorMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

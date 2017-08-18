package com.ap.database.utils.model.service;

/**
 * Сервис
 */
public class Service {

    /** Идентификатор */
    private Long id;
    /** Код сервиса */
    private String code;

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
}

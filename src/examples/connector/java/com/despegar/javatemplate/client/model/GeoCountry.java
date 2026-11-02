package com.despegar.javatemplate.client.model;

import java.time.ZonedDateTime;

public class GeoCountry {
    private String id;
    private String gid;
    private ZonedDateTime lastUpdateDate;
    private String code;
    private String capitalId;
    private String capitalGid;
    private GeoCapital capital;
    private String alternativeCode;
    private String continentId;
    private String continentGid;
    private String phoneCode;
    private String intlAccessCode;

    public GeoCountry() {
    }

    public GeoCountry(String id, String gid, ZonedDateTime lastUpdateDate, String code, String capitalId, String capitalGid, GeoCapital capital, String alternativeCode, String continentId, String continentGid, String phoneCode, String intlAccessCode) {
        this.id = id;
        this.gid = gid;
        this.lastUpdateDate = lastUpdateDate;
        this.code = code;
        this.capitalId = capitalId;
        this.capitalGid = capitalGid;
        this.capital = capital;
        this.alternativeCode = alternativeCode;
        this.continentId = continentId;
        this.continentGid = continentGid;
        this.phoneCode = phoneCode;
        this.intlAccessCode = intlAccessCode;
    }

    public String getId() {
        return id;
    }

    public String getGid() {
        return gid;
    }

    public ZonedDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public String getCode() {
        return code;
    }

    public String getCapitalId() {
        return capitalId;
    }

    public String getCapitalGid() {
        return capitalGid;
    }

    public GeoCapital getCapital() {
        return capital;
    }

    public String getAlternativeCode() {
        return alternativeCode;
    }

    public String getContinentId() {
        return continentId;
    }

    public String getContinentGid() {
        return continentGid;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public String getIntlAccessCode() {
        return intlAccessCode;
    }
}

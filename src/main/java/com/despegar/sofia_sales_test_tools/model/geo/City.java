package com.despegar.sofia_sales_test_tools.model.geo;

import java.util.Map;

public record City(String id,
                   String gid,
                   Map<String, String> descriptions,
                   String code,
                   Location location,
                   String timezone,
                   Double timezoneOffset,
                   String administrativeDivisionId,
                   String administrativeDivisionGid,
                   String countryId,
                   String countryGid) {
}
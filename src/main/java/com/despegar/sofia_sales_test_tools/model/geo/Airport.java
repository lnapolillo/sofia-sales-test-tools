package com.despegar.sofia_sales_test_tools.model.geo;

import java.util.Map;

public record Airport(String code,
                      String cityId,
                      Map<String, String> descriptions,
                      Location location,
                      String id,
                      String gid,
                      String type,
                      Boolean commercial,
                      Long manualOrder) {
}

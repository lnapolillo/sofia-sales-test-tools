package com.despegar.javatemplate.config.connector;

import com.despegar.javatemplate.config.connector.model.JsonFormat;
import com.despegar.javatemplate.config.connector.model.JsonProperties;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

class MapperFactoryTest {

    private static final String ZONE_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss:SSSSSSSSS z";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss:SSSSSSSSS";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss:SSSSSSSSS";

    @Test
    void mapperFactoryTest_snakeCase_deserialize_ok() throws JsonProcessingException {
        var object = this.generateObjectForTest();

        var objectMapper = MapperFactory.createMapper(JsonProperties.of(JsonFormat.SNAKE_CASE));
        var result = objectMapper.writeValueAsString(object);

        Assertions.assertEquals(this.getExpectedSnakeCaseJson(),result);
    }

    @Test
    void mapperFactoryTest_camelCase_deserialize_ok() throws JsonProcessingException {
        var object = this.generateObjectForTest();

        var objectMapper = MapperFactory.createMapper(JsonProperties.of(JsonFormat.CAMEL_CASE));
        var result = objectMapper.writeValueAsString(object);

        Assertions.assertEquals(this.getExpectedCamelCaseJson(),result);
    }

    @Test
    void mapperFactoryTest_snakeCase_deserialize_customDatePatterns_ok() throws JsonProcessingException {
        var object = this.generateObjectForTest();
        var properties = new JsonProperties(JsonFormat.SNAKE_CASE,ZONE_DATE_TIME_PATTERN,DATE_TIME_PATTERN,DATE_PATTERN,TIME_PATTERN);
        var objectMapper = MapperFactory.createMapper(properties);
        var result = objectMapper.writeValueAsString(object);

        Assertions.assertEquals(this.getExpectedSnakeCaseWithCustomDatePatternsJson(),result);
    }

    @Test
    void mapperFactoryTest_snakeCase_serialize_ok() throws JsonProcessingException {
        var json = this.getExpectedSnakeCaseJson();

        var objectMapper = MapperFactory.createMapper(JsonProperties.of(JsonFormat.SNAKE_CASE));
        var result = objectMapper.readValue(json,ObjectMockForTest.class);

        var objectExpected = this.generateObjectForTest();
        Assertions.assertEquals(objectExpected.getId(),result.getId());
        Assertions.assertEquals(objectExpected.getRetries(),result.getRetries());
        Assertions.assertEquals(objectExpected.getName(),result.getName());
        Assertions.assertEquals(0, objectExpected.getAmount().compareTo(result.getAmount()));
        Assertions.assertEquals(objectExpected.getDate(),result.getDate());
        Assertions.assertEquals(objectExpected.getLocalDateTime(),result.getLocalDateTime());
        Assertions.assertEquals(objectExpected.getLocalDate(),result.getLocalDate());
        Assertions.assertEquals(objectExpected.getListString().size(),result.getListString().size());
        Assertions.assertEquals(objectExpected.getListString().size(),result.getListString().size());
        Assertions.assertEquals(objectExpected.getListString().getFirst(),result.getListString().getFirst());
        Assertions.assertNotNull(result.getSubObjectMockForTest());
        Assertions.assertEquals(objectExpected.getSubObjectMockForTest().getId(),result.getSubObjectMockForTest().getId());
        Assertions.assertNotNull(result.getMapOfSubObjectsForTest());
        Assertions.assertEquals(objectExpected.getMapOfSubObjectsForTest().size(),result.getMapOfSubObjectsForTest().size());
        Assertions.assertNotNull(result.getMapOfSubObjectsForTest().get("key1"));
        Assertions.assertEquals(objectExpected.getMapOfSubObjectsForTest().get("key1").getId(),result.getMapOfSubObjectsForTest().get("key1").getId());
        Assertions.assertNull(result.getNullValueInTest());
    }

    @Test
    void mapperFactoryTest_snakeCase_serialize_withCustomDatePattern_ok() throws JsonProcessingException {
        var json = this.getExpectedSnakeCaseWithCustomDatePatternsJson();

        var properties = new JsonProperties(JsonFormat.SNAKE_CASE,ZONE_DATE_TIME_PATTERN,DATE_TIME_PATTERN,DATE_PATTERN,TIME_PATTERN);
        var objectMapper = MapperFactory.createMapper(properties);
        var result = objectMapper.readValue(json,ObjectMockForTest.class);

        var objectExpected = this.generateObjectForTest();
        Assertions.assertEquals(objectExpected.getId(),result.getId());
        Assertions.assertEquals(objectExpected.getRetries(),result.getRetries());
        Assertions.assertEquals(objectExpected.getName(),result.getName());
        Assertions.assertEquals(0, objectExpected.getAmount().compareTo(result.getAmount()));
        Assertions.assertEquals(objectExpected.getDate(),result.getDate());
        Assertions.assertEquals(objectExpected.getLocalDateTime(),result.getLocalDateTime());
        Assertions.assertEquals(objectExpected.getLocalDate(),result.getLocalDate());
        Assertions.assertEquals(objectExpected.getListString().size(),result.getListString().size());
        Assertions.assertEquals(objectExpected.getListString().size(),result.getListString().size());
        Assertions.assertEquals(objectExpected.getListString().getFirst(),result.getListString().getFirst());
        Assertions.assertNotNull(result.getSubObjectMockForTest());
        Assertions.assertEquals(objectExpected.getSubObjectMockForTest().getId(),result.getSubObjectMockForTest().getId());
        Assertions.assertNotNull(result.getMapOfSubObjectsForTest());
        Assertions.assertEquals(objectExpected.getMapOfSubObjectsForTest().size(),result.getMapOfSubObjectsForTest().size());
        Assertions.assertNotNull(result.getMapOfSubObjectsForTest().get("key1"));
        Assertions.assertEquals(objectExpected.getMapOfSubObjectsForTest().get("key1").getId(),result.getMapOfSubObjectsForTest().get("key1").getId());
        Assertions.assertNull(result.getNullValueInTest());
    }

    @Test
    void mapperFactoryTest_camelCase_serialize_ok() throws JsonProcessingException {
        var json = this.getExpectedCamelCaseJson();

        var objectMapper = MapperFactory.createMapper(JsonProperties.of(JsonFormat.CAMEL_CASE));
        var result = objectMapper.readValue(json,ObjectMockForTest.class);

        var objectExpected = this.generateObjectForTest();
        Assertions.assertEquals(objectExpected.getId(),result.getId());
        Assertions.assertEquals(objectExpected.getRetries(),result.getRetries());
        Assertions.assertEquals(objectExpected.getName(),result.getName());
        Assertions.assertEquals(0, objectExpected.getAmount().compareTo(result.getAmount()));
        Assertions.assertEquals(objectExpected.getDate(),result.getDate());
        Assertions.assertEquals(objectExpected.getLocalDateTime(),result.getLocalDateTime());
        Assertions.assertEquals(objectExpected.getLocalDate(),result.getLocalDate());
        Assertions.assertEquals(objectExpected.getListString().size(),result.getListString().size());
        Assertions.assertEquals(objectExpected.getListString().size(),result.getListString().size());
        Assertions.assertEquals(objectExpected.getListString().getFirst(),result.getListString().getFirst());
        Assertions.assertNotNull(result.getSubObjectMockForTest());
        Assertions.assertEquals(objectExpected.getSubObjectMockForTest().getId(),result.getSubObjectMockForTest().getId());
        Assertions.assertNotNull(result.getMapOfSubObjectsForTest());
        Assertions.assertEquals(objectExpected.getMapOfSubObjectsForTest().size(),result.getMapOfSubObjectsForTest().size());
        Assertions.assertNotNull(result.getMapOfSubObjectsForTest().get("key1"));
        Assertions.assertEquals(objectExpected.getMapOfSubObjectsForTest().get("key1").getId(),result.getMapOfSubObjectsForTest().get("key1").getId());
        Assertions.assertNull(result.getNullValueInTest());
    }

    private ObjectMockForTest generateObjectForTest(){
        var object = new ObjectMockForTest();
        object.setAmount(new BigDecimal("12.99"));
        object.setId(1313131L);
        object.setName("Firstname Lastname");
        object.setNullValueInTest(null);
        object.setRetries(3);
        object.setLocalDate(LocalDate.of(2022,1,22));
        object.setLocalDateTime(LocalDateTime.of(2022,1,22,12,59,30,45));
        object.setDate(Date.from(LocalDateTime.of(2022,1,22,12,59,30,45).toInstant(ZoneOffset.UTC)));
        object.setLocalTime(LocalTime.of(12,59,51,45));
        object.setZonedDateTime(ZonedDateTime.of(LocalDateTime.of(2022,1,22,12,59,30,45),ZoneId.of("UTC")));
        object.setListString(Lists.newArrayList("value1","value2"));
        object.setSubObjectMockForTest(new SubObjectMockForTest(23L));
        object.setMapOfSubObjectsForTest(Maps.newHashMap(Collections.singletonMap("key1", new SubObjectMockForTest(1L))));
        return object;
    }

    private String getExpectedSnakeCaseJson(){
        return "{" +
                    "\"id\":1313131," +
                    "\"retries\":3," +
                    "\"name\":\"Firstname Lastname\"," +
                    "\"amount\":12.99," +
                    "\"local_date_time\":[2022,1,22,12,59,30,45]," +
                    "\"local_date\":[2022,1,22]," +
                    "\"date\":1642856370000," +
                    "\"list_string\":[\"value1\",\"value2\"]," +
                    "\"sub_object_mock_for_test\":{" +
                        "\"id\":23" +
                    "}," +
                    "\"map_of_sub_objects_for_test\":{" +
                        "\"key1\":{" +
                            "\"id\":1" +
                        "}" +
                    "}," +
                "\"zoned_date_time\":1642856370.000000045," +
                "\"local_time\":[12,59,51,45]" +
                "}";
    }

    private String getExpectedSnakeCaseWithCustomDatePatternsJson(){
        return "{" +
                "\"id\":1313131," +
                "\"retries\":3," +
                "\"name\":\"Firstname Lastname\"," +
                "\"amount\":12.99," +
                "\"local_date_time\":\"2022-01-22 12:59:30:000000045\"," +
                "\"local_date\":\"2022-01-22\"," +
                "\"date\":1642856370000," +
                "\"list_string\":[\"value1\",\"value2\"]," +
                "\"sub_object_mock_for_test\":{" +
                "\"id\":23" +
                "}," +
                "\"map_of_sub_objects_for_test\":{" +
                "\"key1\":{" +
                "\"id\":1" +
                "}" +
                "}," +
                "\"zoned_date_time\":\"2022-01-22 12:59:30:000000045 UTC\"," +
                "\"local_time\":\"12:59:51:000000045\"" +
                "}";
    }

    private String getExpectedCamelCaseJson(){
        return "{" +
                    "\"id\":1313131," +
                    "\"retries\":3," +
                    "\"name\":\"Firstname Lastname\"," +
                    "\"amount\":12.99," +
                    "\"localDateTime\":[2022,1,22,12,59,30,45]," +
                    "\"localDate\":[2022,1,22]," +
                    "\"date\":1642856370000," +
                    "\"listString\":[\"value1\",\"value2\"]," +
                    "\"subObjectMockForTest\":{" +
                        "\"id\":23" +
                    "}," +
                    "\"mapOfSubObjectsForTest\":{" +
                        "\"key1\":{" +
                            "\"id\":1" +
                        "}" +
                    "}," +
                    "\"zonedDateTime\":1642856370.000000045," +
                    "\"localTime\":[12,59,51,45]" +
                "}";
    }

    private static class SubObjectMockForTest {

        private Long id;

        @JsonCreator
        public SubObjectMockForTest(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    private static class ObjectMockForTest {
        private Long id;
        private Integer retries;
        private String name;
        private BigDecimal amount;
        private LocalDateTime localDateTime;
        private LocalDate localDate;
        private Date date;
        private List<String> listString;
        private SubObjectMockForTest subObjectMockForTest;
        private Map<String, SubObjectMockForTest> mapOfSubObjectsForTest;
        private String nullValueInTest;
        private ZonedDateTime zonedDateTime;
        private LocalTime localTime;

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

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Integer getRetries() {
            return retries;
        }

        public void setRetries(Integer retries) {
            this.retries = retries;
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        public String getNullValueInTest() {
            return nullValueInTest;
        }

        public void setNullValueInTest(String nullValueInTest) {
            this.nullValueInTest = nullValueInTest;
        }

        public List<String> getListString() {
            return listString;
        }

        public void setListString(List<String> listString) {
            this.listString = listString;
        }

        public SubObjectMockForTest getSubObjectMockForTest() {
            return subObjectMockForTest;
        }

        public void setSubObjectMockForTest(SubObjectMockForTest subObjectMockForTest) {
            this.subObjectMockForTest = subObjectMockForTest;
        }

        public Map<String, SubObjectMockForTest> getMapOfSubObjectsForTest() {
            return mapOfSubObjectsForTest;
        }

        public void setMapOfSubObjectsForTest(Map<String,SubObjectMockForTest> mapOfSubObjectsForTest) {
            this.mapOfSubObjectsForTest = mapOfSubObjectsForTest;
        }

        public ZonedDateTime getZonedDateTime() {
            return zonedDateTime;
        }

        public void setZonedDateTime(ZonedDateTime zonedDateTime) {
            this.zonedDateTime = zonedDateTime;
        }

        public LocalTime getLocalTime() {
            return localTime;
        }

        public void setLocalTime(LocalTime localTime) {
            this.localTime = localTime;
        }
    }

}

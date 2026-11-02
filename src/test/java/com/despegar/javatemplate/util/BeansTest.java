package com.despegar.javatemplate.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class BeansTest {
    @ParameterizedTest
    @CsvSource({
            "apple,         apple",
            "banana,        banana",
            "lower-middle,  lowerMiddle",
            "lower_under,   lowerUnder",
            "UPPER-MIDDLE,  upperMiddle",
            "UPPER_UNDER,   upperUnder",
            "skip--empty,   skipEmpty"
    })
    void toBeanName(String name, String expected) {
        assertEquals(expected, Beans.toBeanName(name));
    }
}
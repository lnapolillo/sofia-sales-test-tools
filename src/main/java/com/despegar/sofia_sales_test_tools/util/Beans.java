package com.despegar.sofia_sales_test_tools.util;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Character.toUpperCase;

public class Beans {

    private Beans() {}

    public static String toBeanName(String name) {
        var words = Arrays.stream(name.split("[-_]"))
                .filter(word -> !word.isEmpty())
                .toList();

        return IntStream.range(0, words.size())
                .mapToObj(idx -> {
                    Supplier<String> firstWordSupplier = () -> words.size() == 1 ? words.get(idx) : words.get(idx).toLowerCase();
                    Supplier<String> otherWordsSupplier = () -> toUpperCase(words.get(idx).charAt(0)) + words.get(idx).substring(1).toLowerCase();
                    return idx == 0 ? firstWordSupplier : otherWordsSupplier;
                })
                .map(Supplier::get)
                .collect(Collectors.joining());
    }
}

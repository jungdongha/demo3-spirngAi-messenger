package com.example.demo3springaimessenger.global.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JavaUtil {

    @SafeVarargs
    public final <T> List<T> merge(List<T>... lists) {
        if (lists == null) {
            return new ArrayList<>();
        }
        List<T> mergedList = new ArrayList<>();

        for (List<T> list : lists) {
            if (list != null) {
                mergedList.addAll(list);
            }
        }
        return mergedList;
    }

}

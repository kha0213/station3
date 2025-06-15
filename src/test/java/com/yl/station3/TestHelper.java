package com.yl.station3;

import java.lang.reflect.Field;

public class TestHelper {
    // 모든 Entity는 Long인 pk값을 가지고 있어서 테스트를 위해 id set하는 함수
    public static <T> void setId(T entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

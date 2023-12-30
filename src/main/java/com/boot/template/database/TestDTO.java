package com.boot.template.database;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TestDTO {
    public String name;
    public String value1;
    public String value2;

    public static TestDTO fromMap(Map<String, Object> map) {
        return new TestDTO(
            (String) map.get("name"),
            (String) map.get("value1"),
            (String) map.get("value2"));
    }
}

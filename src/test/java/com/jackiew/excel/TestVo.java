package com.jackiew.excel;

import javax.validation.constraints.NotBlank;

import com.jackiew.excel.annotation.CellInfo;
import com.jackiew.excel.annotation.ExcelExportClass;

@ExcelExportClass
public class TestVo {
    @CellInfo(title = "username", columnIndex = 0)
    @NotBlank(message = "username cannot be null")
    private String name;
    @CellInfo(title = "age in law", columnIndex = 1)
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "TestVo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

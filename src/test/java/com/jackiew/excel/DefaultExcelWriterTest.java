package com.jackiew.excel;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

public class DefaultExcelWriterTest {

    @Test
    public void write() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        List<TestVo> dataList = new ArrayList<>();
        TestVo vo1 = new TestVo();
        vo1.setName("jackiew");
        vo1.setAge(18);
        dataList.add(vo1);
        TestVo vo2 = new TestVo();
        vo2.setName("tom");
        vo2.setAge(20);
        dataList.add(vo2);
        ExcelWriter writer = new DefaultExcelWriter("result", workbook, dataList, TestVo.class);
        writer.write();
        try (FileOutputStream outputStream = new FileOutputStream("test-output.xlsx");) {
            workbook.write(outputStream);
        }

    }
}
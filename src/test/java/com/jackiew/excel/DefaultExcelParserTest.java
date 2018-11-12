package com.jackiew.excel;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

public class DefaultExcelParserTest {


    @Test
    public void parse() throws IOException {
        Workbook workbook = new XSSFWorkbook(DefaultExcelParserTest.class.getResourceAsStream("/test-input.xlsx"));
        ExcelParser<TestVo> parser = new DefaultExcelParser<>(TestVo.class);
        List<TestVo> result = parser.parse(workbook.getSheetAt(0));
        System.out.println(result);
    }

    @Test()
    public void parseInvalidData() throws IOException {
        Workbook workbook = new XSSFWorkbook(DefaultExcelParserTest.class.getResourceAsStream("/test-input-validation.xlsx"));
        ExcelParser<TestVo> parser = new DefaultExcelParser<>(TestVo.class);
        List<TestVo> result = parser.parse(workbook.getSheetAt(0));
        System.out.println(result);
    }
}
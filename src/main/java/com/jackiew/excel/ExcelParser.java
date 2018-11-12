package com.jackiew.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

public interface ExcelParser<T> {
    List<T> parse(Sheet sheet);
}

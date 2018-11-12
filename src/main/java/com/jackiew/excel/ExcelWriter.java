package com.jackiew.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

public interface ExcelWriter {
    /**
     * write the content of data list into specified workbook with the sheet name
     */
    <T> void write();
}

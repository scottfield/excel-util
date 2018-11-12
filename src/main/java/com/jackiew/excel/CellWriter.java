package com.jackiew.excel;

import java.util.Date;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Cell;

/**
 * write cell value
 */
class CellWriter {
    void write(Cell cell, Object value) {
        if (Objects.isNull(value)) {
            cell.setCellValue("");
            return;
        }
        //set value according value's type
        if (value instanceof Integer) {
            cell.setCellValue((int) value);
        } else if (value instanceof Double) {
            cell.setCellValue((double) value);
        } else if (value instanceof Float) {
            cell.setCellValue((float) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }
}

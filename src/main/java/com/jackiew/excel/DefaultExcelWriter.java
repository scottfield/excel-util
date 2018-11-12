package com.jackiew.excel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.jackiew.excel.annotation.CellInfo;
import com.jackiew.excel.annotation.ExcelExportClass;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class DefaultExcelWriter implements ExcelWriter {
    private int currentRowIndex = 0;
    private final String sheetName;
    private final Workbook workbook;
    private final List<?> dataSet;
    private final LinkedMap<String, Method> headers = new LinkedMap<>();
    private CellWriter cellWriter = new CellWriter();

    public DefaultExcelWriter(String sheetName, Workbook workbook, List<?> dataList, Class<?> supportClass) {
        Objects.requireNonNull(sheetName, "sheet name is required");
        Objects.requireNonNull(workbook, "workbook is required");
        Objects.requireNonNull(dataList, "data set is required");
        this.sheetName = sheetName;
        this.workbook = workbook;
        this.dataSet = dataList;

        if (!supportClass.isAnnotationPresent(ExcelExportClass.class)) {
            throw new IllegalArgumentException("@ExcelExportClass must be present on supported class parameter" + supportClass.getName());
        }
        Field[] fields = supportClass.getDeclaredFields();
        Arrays.sort(fields, FieldComparator.getComparator());
        for (Field field : fields) {
            CellInfo cellInfo = field.getAnnotation(CellInfo.class);
            String methodName = "get" + StringUtils.capitalize(field.getName());
            try {
                Method method = supportClass.getMethod(methodName);
                headers.put(cellInfo.title(), method);
            } catch (NoSuchMethodException e) {
                throw new NotFoundException("method:" + methodName + " not found on class " + supportClass.getName() + ",please confirm you have defined this getter method");
            }
        }
    }


    private void writeHeader(Row row) {
        int i = 0;
        for (String title : headers.keySet()) {
            Cell cell = row.createCell(i++);
            cell.setCellValue(title);

        }

    }

    public void write() {
        Sheet sheet = workbook.createSheet(sheetName);
        writeHeader(sheet.createRow(currentRowIndex++));
        for (Object data : dataSet) {
            Row row = sheet.createRow(currentRowIndex++);
            writeRow(data, row);
        }
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        CellRangeAddress c = CellRangeAddress.valueOf("K1");
        sheet.setAutoFilter(c);
    }

    private void writeRow(Object data, Row row) {
        int i = 0;
        for (Method method : headers.values()) {
            Cell cell = row.createCell(i++);
            writeCellValue(data, method, cell);
        }
    }

    private void writeCellValue(Object data, Method method, Cell cell) {
        Object value = null;
        try {
            value = MethodUtils.invokeMethod(data, method.getName(), null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        cellWriter.write(cell, value);
    }
}

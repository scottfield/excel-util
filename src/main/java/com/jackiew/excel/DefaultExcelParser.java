package com.jackiew.excel;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.jackiew.excel.annotation.CellInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * this class support convert excel row into a bean instance of type T
 *
 * @param <T>
 */
public class DefaultExcelParser<T> implements ExcelParser<T> {
    /**
     * whether input excel contains the title row
     */
    private boolean hasTitle = true;
    /**
     * the bean class used to create an instance to wrapper the row's value
     * this class's field should be annotation with @CellInfo
     */
    private Class<T> supportClazz;
    private Map<String, Integer> fields;
    private static final Validator VALIDATOR;

    static {
        ValidatorFactory factory = Validation
                .buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();
    }

    public DefaultExcelParser(Class<T> supportClazz) {
        this.supportClazz = supportClazz;
        Field[] fields = FieldUtils.getFieldsWithAnnotation(supportClazz, CellInfo.class);
        if (fields.length == 0) {
            throw new IllegalArgumentException(" please at least annotation one field with CellInfo class");
        }
        this.fields = Arrays.stream(fields).collect(toMap(Field::getName, field -> {
            CellInfo cellInfo = field.getDeclaredAnnotation(CellInfo.class);
            return cellInfo.columnIndex();
        }));
    }

    public void setHasTitle(boolean hasTitle) {
        this.hasTitle = hasTitle;
    }

    @Override
    public List<T> parse(Sheet sheet) {
        List<T> rioList = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.iterator();
        if (hasTitle) {
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
        }

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Object target = null;
            try {
                target = ConstructorUtils.invokeConstructor(supportClazz);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("class " + supportClazz.getName() + " not has non argument constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("class " + supportClazz.getName() + " should has public visibility non argument constructor", e);
            } catch (InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }

            for (Map.Entry<String, Integer> field : fields.entrySet()) {
                int columnIndex = field.getValue();
                Cell cell = row.getCell(columnIndex);
                Object cellValue = getCellValue(cell);
                String fieldName = field.getKey();
                try {
                    BeanUtils.setProperty(target, fieldName, cellValue);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            Set<ConstraintViolation<Object>> constraintViolations = VALIDATOR.validate(target);

            if (CollectionUtils.isNotEmpty(constraintViolations)) {
                String error = constraintViolations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(joining(";"));
                throw new IllegalArgumentException(error);
            }
            rioList.add((T) target);
        }
        return rioList;
    }

    private Object getCellValue(Cell cell) {
        if (Objects.isNull(cell)) {
            return null;
        }
        if (CellType.STRING.equals(cell.getCellType())) {
            return cell.getStringCellValue();
        }
        if (CellType.NUMERIC.equals(cell.getCellType())) {
            Double value = cell.getNumericCellValue();
            return value.intValue();
        }
        throw new IllegalArgumentException("not supported cell type");
    }
}

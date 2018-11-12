package com.jackiew.excel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CellInfo {
    /**
     * column title which will be write into the title row
     *
     * @return title name
     */
    String title();

    /**
     * column index from where to read cell value
     *
     * @return column index which is zero based
     */
    int columnIndex();
}

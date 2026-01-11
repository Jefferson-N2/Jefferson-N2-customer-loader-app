package com.corporate.payroll.application.mapper;

import org.mapstruct.Named;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

/**
 * Conversiones comunes para MapStruct mappers
 */
public class CommonMapperMethods {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Named("localDateToString")
    public static String localDateToString(LocalDate date) {
        if (date == null){
            return null;
        }
        return date.format(DATE_FORMATTER);
    }
    
    @Named("bigDecimalToString")
    public static String bigDecimalToString(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.toPlainString();
    }
}

package com.corporate.payroll.application.util;

/**
 * Constantes para el procesamiento de archivos de carga masiva de clientes.
 * Define headers, índices de columnas y valores por defecto.
 */
public class FileProcessingConstants {
    
    public static final String HEADER_ID_TYPE = "Tipo identificacion";
    public static final String HEADER_ID_NUMBER = "Numero identificacion";
    public static final String HEADER_JOIN_DATE = "Fecha ingreso";
    public static final String HEADER_PAYROLL_VALUE = "Valor nomina";
    public static final String HEADER_EMAIL = "Email";
    public static final String HEADER_PHONE = "Numero celular";
    
    public static final String[] REQUIRED_HEADERS = {
        HEADER_ID_TYPE,
        HEADER_ID_NUMBER,
        HEADER_JOIN_DATE,
        HEADER_PAYROLL_VALUE,
        HEADER_EMAIL,
        HEADER_PHONE
    };
    
    public static final int INDEX_ID_TYPE = 0;
    public static final int INDEX_ID_NUMBER = 1;
    public static final int INDEX_JOIN_DATE = 2;
    public static final int INDEX_PAYROLL_VALUE = 3;
    public static final int INDEX_EMAIL = 4;
    public static final int INDEX_PHONE = 5;
    
    public static final int MIN_COLUMNS_REQUIRED = 6;
    
    public static final String DEFAULT_ACCOUNT_STATUS = "ACTIVE";
    public static final String DEFAULT_CLIENT_CODE_PREFIX = "CLI";
    public static final String DEFAULT_ACCOUNT_CODE_PREFIX = "ACC";
    public static final int HEADER_ROW = 1;

    public enum ErrorType {
        INVALID_HEADERS("INVALID_HEADERS"),
        MISSING_FIELD("MISSING_FIELD"),
        INVALID_FORMAT("INVALID_FORMAT"),
        VALIDATION_ERROR("VALIDATION_ERROR"),
        DUPLICATE_CLIENT("DUPLICATE_CLIENT"),
        NOT_FOUND_IN_DATABOOK("NOT_FOUND_IN_DATABOOK"),
        FILE_READ_ERROR("FILE_READ_ERROR"),
        SYSTEM_ERROR("SYSTEM_ERROR");
        
        private final String value;
        
        ErrorType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }

    private FileProcessingConstants() {
    }
}

package com.corporate.payroll.application.util;

/**
 * Utilidad para validación y normalización de parámetros de paginación
 */
public class PaginationHelper {
    
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    
    /**
     * Valida y normaliza el número de página
     * @param page página solicitada (puede ser negativa)
     * @return página válida (mínimo 0)
     */
    public static int validatePage(int page) {
        if (page < 0) {
            return DEFAULT_PAGE;
        }
        return page;
    }
    
    /**
     * Valida y normaliza el tamaño de página
     * @param size tamaño solicitado
     * @return tamaño válido (mínimo DEFAULT_SIZE, máximo MAX_SIZE)
     */
    public static int validateSize(int size) {
        if (size < 1) {
            return DEFAULT_SIZE;
        }
        if (size > MAX_SIZE) {
            return MAX_SIZE;
        }
        return size;
    }
    
    /**
     * Calcula el offset para la consulta SQL
     * @param page número de página (0-indexed)
     * @param size tamaño de página
     * @return offset para la consulta
     */
    public static long calculateOffset(int page, int size) {
        return (long) page * size;
    }
    
    /**
     * Valida y normaliza todos los parámetros de paginación de una vez
     * @param page página solicitada
     * @param size tamaño solicitado
     * @return array con [página válida, tamaño válido]
     */
    public static int[] validatePagination(int page, int size) {
        return new int[]{validatePage(page), validateSize(size)};
    }
}

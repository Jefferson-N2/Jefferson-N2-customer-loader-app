package com.corporate.payroll.application.port.out;

import com.corporate.payroll.domain.model.Payment;
import java.util.List;

public interface PaymentRepositoryPort {
    
    /**
     * Guarda un pago en la base de datos
     * @param payment el pago a guardar
     * @return el pago guardado con ID asignado
     */
    Payment save(Payment payment);
    
    /**
     * Obtiene pagos de una cuenta de forma paginada
     * @param accountNumber número de cuenta
     * @param offset desplazamiento de la página
     * @param limit cantidad de registros por página
     * @return lista de pagos
     */
    List<Payment> findByAccountNumber(String accountNumber, int offset, int limit);
    
    /**
     * Cuenta el total de pagos de una cuenta
     * @param accountNumber número de cuenta
     * @return cantidad total de pagos
     */
    long countByAccountNumber(String accountNumber);
}

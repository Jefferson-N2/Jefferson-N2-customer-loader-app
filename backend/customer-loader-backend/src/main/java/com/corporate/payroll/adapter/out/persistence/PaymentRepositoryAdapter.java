package com.corporate.payroll.adapter.out.persistence;

import com.corporate.payroll.adapter.out.persistence.entity.PayrollPaymentEntity;
import com.corporate.payroll.adapter.out.persistence.entity.AccountEntity;
import com.corporate.payroll.application.port.out.PaymentRepositoryPort;
import com.corporate.payroll.domain.model.Payment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para Payment
 * Maneja la conversión entre modelos de dominio y entidades JPA
 * Utiliza PayrollPaymentEntity para la persistencia
 */
@ApplicationScoped
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    @Transactional
    public Payment save(Payment payment) {
        // Obtener la cuenta por accountNumber
        AccountEntity account = entityManager.createQuery(
                "SELECT a FROM AccountEntity a WHERE a.accountNumber = :accountNumber",
                AccountEntity.class)
                .setParameter("accountNumber", payment.getAccountNumber())
                .getSingleResult();
        
        PayrollPaymentEntity entity = PayrollPaymentEntity.builder()
                .account(account)
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .status(payment.getStatus() != null ? payment.getStatus() : "PENDING")
                .build();
        
        entityManager.persist(entity);
        entityManager.flush();
        
        return toDomain(entity);
    }
    
    @Override
    public List<Payment> findByAccountNumber(String accountNumber, int offset, int limit) {
        return entityManager.createQuery(
                "SELECT p FROM PayrollPaymentEntity p " +
                "WHERE p.account.accountNumber = :accountNumber " +
                "ORDER BY p.paymentDate DESC", 
                PayrollPaymentEntity.class)
                .setParameter("accountNumber", accountNumber)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public long countByAccountNumber(String accountNumber) {
        return entityManager.createQuery(
                "SELECT COUNT(p) FROM PayrollPaymentEntity p " +
                "WHERE p.account.accountNumber = :accountNumber",
                Long.class)
                .setParameter("accountNumber", accountNumber)
                .getSingleResult();
    }
    
    private Payment toDomain(PayrollPaymentEntity entity) {
        return Payment.builder()
                .id(entity.getId())
                .accountNumber(entity.getAccount().getAccountNumber())
                .paymentDate(entity.getPaymentDate())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .build();
    }
}

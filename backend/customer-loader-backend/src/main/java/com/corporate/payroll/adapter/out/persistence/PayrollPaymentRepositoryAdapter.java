package com.corporate.payroll.adapter.out.persistence;

import com.corporate.payroll.application.port.out.PayrollPaymentRepositoryPort;
import com.corporate.payroll.domain.model.PayrollPayment;
import com.corporate.payroll.adapter.out.persistence.entity.PayrollPaymentEntity;
import com.corporate.payroll.adapter.out.persistence.mapper.PayrollPaymentPersistenceMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PayrollPaymentRepositoryAdapter implements PayrollPaymentRepositoryPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private PayrollPaymentPersistenceMapper paymentMapper;

    @Override
    @Transactional
    public void save(PayrollPayment payment) {
        PayrollPaymentEntity entity = paymentMapper.toDomainEntity(payment);
        entityManager.persist(entity);
    }

    @Override
    public List<PayrollPayment> findByAccountNumber(String accountNumber) {
        List<Object[]> results = entityManager.createQuery(
                        "SELECT p.id, p.paymentDate, p.amount, p.status, a.id FROM PayrollPaymentEntity p JOIN p.account a WHERE a.accountNumber = :accountNumber ORDER BY p.paymentDate DESC",
                        Object[].class)
                .setParameter("accountNumber", accountNumber)
                .getResultList();
        
        return results.stream()
                .map(row -> PayrollPayment.builder()
                        .id((Long) row[0])
                        .paymentDate((java.time.LocalDate) row[1])
                        .amount((java.math.BigDecimal) row[2])
                        .status((String) row[3])
                        .accountId((Long) row[4])
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<PayrollPayment> findByAccountNumber(String accountNumber, int page, int size) {
        List<Object[]> results = entityManager.createQuery(
                        "SELECT p.id, p.paymentDate, p.amount, p.status, a.id FROM PayrollPaymentEntity p JOIN p.account a WHERE a.accountNumber = :accountNumber ORDER BY p.paymentDate DESC",
                        Object[].class)
                .setParameter("accountNumber", accountNumber)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
        
        return results.stream()
                .map(row -> PayrollPayment.builder()
                        .id((Long) row[0])
                        .paymentDate((java.time.LocalDate) row[1])
                        .amount((java.math.BigDecimal) row[2])
                        .status((String) row[3])
                        .accountId((Long) row[4])
                        .build())
                .collect(Collectors.toList());
    }
}
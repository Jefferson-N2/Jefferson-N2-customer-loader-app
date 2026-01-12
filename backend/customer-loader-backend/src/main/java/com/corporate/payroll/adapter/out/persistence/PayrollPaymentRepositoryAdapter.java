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
import java.util.Optional;
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
    public Optional<PayrollPayment> findFirstPaymentByAccountId(Long accountId) {
        try {
            PayrollPaymentEntity payrollPaymentEntity = entityManager.createQuery(
                    "SELECT p FROM PayrollPaymentEntity p WHERE p.accountId = :accountId", 
                    PayrollPaymentEntity.class)
                    .setParameter("accountId", accountId)
                    .setMaxResults(1)
                    .getSingleResult();
            
            return Optional.ofNullable(paymentMapper.toModel(payrollPaymentEntity));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
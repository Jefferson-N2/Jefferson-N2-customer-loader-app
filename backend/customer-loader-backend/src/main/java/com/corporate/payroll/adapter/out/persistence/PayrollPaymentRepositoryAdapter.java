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

/**
 * Adaptador de persistencia para PayrollPayment
 * Maneja la conversión entre modelos de dominio y entidades JPA
 */
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

        Optional.ofNullable(entity.getId())
                .ifPresentOrElse(
                        id ->  entityManager.merge(entity),
                        () -> entityManager.persist(entity)
                );

    }

    @Override
    public List<PayrollPayment> findByAccountId(Long accountId) {
        List<PayrollPaymentEntity> entities = entityManager.createQuery(
                        "SELECT p FROM PayrollPaymentEntity p WHERE p.account.id = :accountId", PayrollPaymentEntity.class)
                .setParameter("accountId", accountId)
                .getResultList();

        return entities.stream()
                .map(paymentMapper::toModel)
                .collect(Collectors.toList());
    }
}

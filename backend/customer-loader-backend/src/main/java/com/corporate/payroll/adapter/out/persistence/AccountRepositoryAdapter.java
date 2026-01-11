package com.corporate.payroll.adapter.out.persistence;

import com.corporate.payroll.application.port.out.AccountRepositoryPort;
import com.corporate.payroll.domain.model.Account;
import com.corporate.payroll.adapter.out.persistence.entity.AccountEntity;
import com.corporate.payroll.adapter.out.persistence.mapper.AccountPersistenceMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para Account
 * Maneja la conversión entre modelos de dominio y entidades JPA
 */
@ApplicationScoped
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private AccountPersistenceMapper accountMapper;

    @Override
    @Transactional
    public void save(Account account) {
        AccountEntity entity = accountMapper.toDomainEntity(account);

        Optional.ofNullable(entity.getId())
                .ifPresentOrElse(id -> entityManager.merge(entity),
                        () -> entityManager.persist(entity));

    }

    @Override
    public Optional<Account> findById(Long id) {
        AccountEntity entity = entityManager.find(AccountEntity.class, id);
        return Optional.ofNullable(entity).map(accountMapper::toModel);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return entityManager.createQuery(
                        "SELECT a FROM AccountEntity a WHERE a.accountNumber = :accountNumber", AccountEntity.class)
                .setParameter("accountNumber", accountNumber)
                .getResultStream()
                .findFirst()
                .map(accountMapper::toModel);
    }

    @Override
    public List<Account> findByClientId(Long clientId) {
        List<AccountEntity> entities = entityManager.createQuery(
                        "SELECT a FROM AccountEntity a WHERE a.clientId = :clientId", AccountEntity.class)
                .setParameter("clientId", clientId)
                .getResultList();
        return entities.stream()
                .map(accountMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(a) FROM AccountEntity a WHERE a.accountNumber = :accountNumber", Long.class)
                .setParameter("accountNumber", accountNumber)
                .getSingleResult();
        return count > 0;
    }
}

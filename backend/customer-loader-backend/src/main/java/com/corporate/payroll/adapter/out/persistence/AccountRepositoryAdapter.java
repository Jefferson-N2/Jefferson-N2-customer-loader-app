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
    public Account save(Account account) {
        AccountEntity entity = accountMapper.toDomainEntity(account);

        if (entity.getId() != null) {
            entity = entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
        entityManager.flush();
        
        return accountMapper.toModel(entity);
    }

    @Override
    public Optional<Account> findByClientId(Long clientId) {
        List<AccountEntity> entities = entityManager.createQuery(
                        "SELECT a FROM AccountEntity a " +
                                "WHERE a.client.id = :clientId", AccountEntity.class)
                .setParameter("clientId", clientId)
                .getResultList();
        return entities.stream()
                .map(accountMapper::toModel)
                .findAny();
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(a) FROM AccountEntity a " +
                                "WHERE a.accountNumber = :accountNumber", Long.class)
                .setParameter("accountNumber", accountNumber)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        List<AccountEntity> entities = entityManager.createQuery(
                        "SELECT a FROM AccountEntity a " +
                                "WHERE a.accountNumber = :accountNumber", AccountEntity.class)
                .setParameter("accountNumber", accountNumber)
                .getResultList();
        return entities.stream()
                .map(accountMapper::toModel)
                .findFirst();
    }

    @Override
    public Long getLastAccountNumber() {
            String lastAccountNumber = entityManager.createQuery(
                            "SELECT a.accountNumber FROM AccountEntity a ORDER BY CAST(a.accountNumber AS long) DESC", String.class)
                    .setMaxResults(1)
                    .getSingleResult();
            return Long.parseLong(lastAccountNumber);
            }
}

package com.corporate.payroll.adapter.out.persistence;

import com.corporate.payroll.application.port.out.ClientRepositoryPort;
import com.corporate.payroll.domain.model.Client;
import com.corporate.payroll.adapter.out.persistence.entity.ClientEntity;
import com.corporate.payroll.adapter.out.persistence.mapper.ClientPersistenceMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para Client
 * Maneja la conversión entre modelos de dominio y entidades JPA
 */
@ApplicationScoped
public class ClientRepositoryAdapter implements ClientRepositoryPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private ClientPersistenceMapper clientMapper;

    @Override
    @Transactional
    public void save(Client client) {
        ClientEntity entity = clientMapper.toDomainEntity(client);
        
          Optional.ofNullable(entity.getId())
                .ifPresentOrElse(id -> entityManager.merge(entity),
                        () -> entityManager.persist(entity));
    }

    @Override
    @Transactional
    public Optional<Client> findById(Long id) {
        ClientEntity entity = entityManager.find(ClientEntity.class, id);
        return Optional.ofNullable(entity).map(clientMapper::toModel);
    }

    @Override
    @Transactional
    public Optional<Client> findByClientCode(String clientCode) {
        try {
            ClientEntity entity = entityManager.createQuery(
                "SELECT c FROM ClientEntity c WHERE c.clientCode = :code", ClientEntity.class)
                .setParameter("code", clientCode)
                .getSingleResult();
            return Optional.of(clientMapper.toModel(entity));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public boolean existsByClientCode(String clientCode) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(c) FROM ClientEntity c WHERE c.clientCode = :code", Long.class)
            .setParameter("code", clientCode)
            .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public boolean existsByIdNumber(String idNumber) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(c) FROM ClientEntity c WHERE c.idNumber = :idNumber", Long.class)
            .setParameter("idNumber", idNumber)
            .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public List<Client> findAllPaginated(long offset, int limit) {
        List<ClientEntity> entities = entityManager.createQuery(
            "SELECT c FROM ClientEntity c ORDER BY c.id", ClientEntity.class)
            .setFirstResult((int) offset)
            .setMaxResults(limit)
            .getResultList();
        return entities.stream()
            .map(clientMapper::toModel)
            .collect(Collectors.toList());
    }

}


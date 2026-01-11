package com.corporate.payroll.adapter.out.persistence;

import com.corporate.payroll.application.port.out.ClientRepositoryPort;
import com.corporate.payroll.domain.model.Client;
import com.corporate.payroll.adapter.out.persistence.entity.ClientEntity;
import com.corporate.payroll.adapter.out.persistence.mapper.ClientPersistenceMapper;
import com.corporate.payroll.adapter.out.persistence.mapper.AccountPersistenceMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para Client
 * Maneja la conversión entre modelos de dominio y entidades JPA
 */
@Slf4j
@ApplicationScoped
public class ClientRepositoryAdapter implements ClientRepositoryPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private ClientPersistenceMapper clientMapper;

    @Inject
    private AccountPersistenceMapper accountMapper;

    @Override
    @Transactional
    public Client save(Client client) {
        log.info("Iniciando persistencia de cliente: {}", client.getClientCode());
        ClientEntity entity = clientMapper.toDomainEntity(client);
        
        if (entity.getId() == null) {
            log.debug("Creando nuevo cliente");
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.refresh(entity);
        } else {
            log.debug("Actualizando cliente existente con ID: {}", entity.getId());
            entityManager.merge(entity);
        }
        
        Client result = clientMapper.toModel(entity);
        log.info("Cliente persistido exitosamente: {} con ID: {}", 
            client.getClientCode(), result.getId());
        return result;
    }

    @Override
    @Transactional
    public Optional<Client> findByClientCode(String clientCode) {
        try {
            ClientEntity entity = entityManager.createQuery(
                "SELECT c FROM ClientEntity c LEFT JOIN FETCH c.accounts WHERE c.clientCode = :code", ClientEntity.class)
                .setParameter("code", clientCode)
                .getSingleResult();
            
            Client client = clientMapper.toModel(entity);
            if (entity.getAccounts() != null && !entity.getAccounts().isEmpty()) {
                client.setAccounts(entity.getAccounts().stream()
                    .map(accountMapper::toModel)
                    .collect(Collectors.toList()));
            }
            
            return Optional.of(client);
        } catch (NoResultException e) {
            return Optional.empty();
        } 
    }

    @Override
    public List<Client> findAllPaginated(int page, int size) {
        List<ClientEntity> entities = entityManager.createQuery(
                "SELECT c FROM ClientEntity c ORDER BY c.id DESC",
                ClientEntity.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
        
        return entities.stream()
                .map(clientMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Client> findByProcessId(String processId, int page, int size) {
        log.debug("Buscando clientes para proceso: {} (página: {}, tamaño: {})", 
            processId, page, size);
        List<ClientEntity> entities = entityManager.createQuery(
                "SELECT c FROM ClientEntity c WHERE c.processId = :processId " +
                "ORDER BY c.id DESC",
                ClientEntity.class)
                .setParameter("processId", processId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
        
        log.info("Se encontraron {} clientes para el proceso {}", 
            entities.size(), processId);
        
        return entities.stream()
                .map(clientMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public long countByProcessId(String processId) {
        log.debug("Contando clientes para proceso: {}", processId);
        long count = entityManager.createQuery(
                "SELECT COUNT(c) FROM ClientEntity c WHERE c.processId = :processId",
                Long.class)
                .setParameter("processId", processId)
                .getSingleResult();
        log.info("Total de clientes en proceso {}: {}", processId, count);
        return count;
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
}
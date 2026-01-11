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

    @Override
    @Transactional
    public Client save(Client client) {
        log.info("Iniciando persistencia de cliente: {}", client.getClientCode());
        try {
            ClientEntity entity = clientMapper.toDomainEntity(client);
            
            Optional.ofNullable(entity.getId())
                    .ifPresentOrElse(id -> {
                        log.debug("Actualizando cliente existente con ID: {}", id);
                        entityManager.merge(entity);
                    },
                    () -> {
                        log.debug("Creando nuevo cliente");
                        entityManager.persist(entity);
                    });
            
            if (entity.getId() == null) {
                entityManager.flush();  // Fuerza la generación del ID
            }
            
            Client result = clientMapper.toModel(entity);
            log.info("Cliente persistido exitosamente: {} con ID: {}", 
                client.getClientCode(), result.getId());
            return result;
        } catch (Exception e) {
            log.error("Error al persistir cliente {}: {}", client.getClientCode(), e.getMessage(), e);
            throw e;
        }
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
        try {
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
        } catch (Exception e) {
            log.error("Error al buscar clientes para proceso {}: {}", 
                processId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public long countByProcessId(String processId) {
        log.debug("Contando clientes para proceso: {}", processId);
        try {
            long count = entityManager.createQuery(
                    "SELECT COUNT(c) FROM ClientEntity c WHERE c.processId = :processId",
                    Long.class)
                    .setParameter("processId", processId)
                    .getSingleResult();
            log.info("Total de clientes en proceso {}: {}", processId, count);
            return count;
        } catch (Exception e) {
            log.error("Error al contar clientes para proceso {}: {}", 
                processId, e.getMessage(), e);
            throw e;
        }
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


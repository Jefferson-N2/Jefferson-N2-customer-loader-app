package com.corporate.payroll.application.port.out;

import com.corporate.payroll.domain.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepositoryPort {

    void save(Client client);
    Optional<Client> findById(Long id);
    boolean existsByIdNumber(String idNumber);

    Optional<Client> findByClientCode(String clientCode);

    boolean existsByClientCode(String clientCode);

    List<Client> findAllPaginated(long offset, int limit);
}
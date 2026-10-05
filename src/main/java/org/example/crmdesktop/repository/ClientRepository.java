package org.example.crmdesktop.repository;

import org.example.crmdesktop.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
}

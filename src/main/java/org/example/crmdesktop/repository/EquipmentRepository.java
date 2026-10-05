package org.example.crmdesktop.repository;

import org.example.crmdesktop.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    @Override
    @EntityGraph(attributePaths = "client")
    List<Equipment> findAll();

    @EntityGraph(attributePaths = "client")
    List<Equipment> findBySerialNumberContainingIgnoreCaseOrderByBrandAsc(String serialNumber);

    @EntityGraph(attributePaths = "client")
    List<Equipment> findByClientIdOrderByBrandAsc(Long clientId);
}

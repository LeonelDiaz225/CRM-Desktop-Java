package org.example.crmdesktop.repository;

import org.example.crmdesktop.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findBySerialNumberContainingIgnoreCaseOrderByBrandAsc(String serialNumber);
    List<Equipment> findByClientIdOrderByBrandAsc(Long clientId);
}

package org.example.crmdesktop.repository;

import org.example.crmdesktop.model.Repair;
import org.example.crmdesktop.model.RepairStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface RepairRepository extends JpaRepository<Repair, Long> {
    long countByStatusNot(RepairStatus status);

    List<Repair> findByClientIdOrderByReceivedDateDesc(Long clientId);

    List<Repair> findByStatusOrderByReceivedDateDesc(RepairStatus status);

    @EntityGraph(attributePaths = {"client", "equipment"})
    List<Repair> findTop10ByOrderByReceivedDateDesc();
}

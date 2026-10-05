package org.example.crmdesktop.service;

import org.example.crmdesktop.model.Repair;
import org.example.crmdesktop.model.RepairStatus;
import org.example.crmdesktop.repository.ClientRepository;
import org.example.crmdesktop.repository.RepairRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {
    private final ClientRepository clientRepository;
    private final RepairRepository repairRepository;

    public DashboardService(ClientRepository clientRepository, RepairRepository repairRepository) {
        this.clientRepository = clientRepository;
        this.repairRepository = repairRepository;
    }

    @Transactional(readOnly = true)
    public long countClients() {
        return clientRepository.count();
    }

    @Transactional(readOnly = true)
    public long countActiveRepairs() {
        return repairRepository.countByStatusNot(RepairStatus.ENTREGADO);
    }

    @Transactional(readOnly = true)
    public List<Repair> findRecentRepairs() {
        return repairRepository.findTop10ByOrderByReceivedDateDesc();
    }
}

package org.example.crmdesktop.service;

import org.example.crmdesktop.model.Repair;
import org.example.crmdesktop.model.RepairStatus;
import org.example.crmdesktop.repository.RepairRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RepairService {
    private final RepairRepository repairRepository;

    public RepairService(RepairRepository repairRepository) {
        this.repairRepository = repairRepository;
    }

    @Transactional(readOnly = true)
    public List<Repair> findAll() {
        return repairRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Repair> findByClientId(Long clientId) {
        return repairRepository.findByClientIdOrderByReceivedDateDesc(clientId);
    }

    @Transactional(readOnly = true)
    public List<Repair> findByStatus(RepairStatus status) {
        return repairRepository.findByStatusOrderByReceivedDateDesc(status);
    }

    @Transactional
    public Repair save(Repair repair) {
        validate(repair);
        if (repair.getStatus() == null) {
            repair.setStatus(RepairStatus.RECIBIDO);
        }
        if (repair.getReceivedDate() == null) {
            repair.setReceivedDate(LocalDate.now());
        }
        return repairRepository.save(repair);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!repairRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe la reparación con id " + id);
        }
        repairRepository.deleteById(id);
    }

    private void validate(Repair repair) {
        if (repair == null || repair.getProblemDescription() == null
                || repair.getProblemDescription().isBlank()) {
            throw new IllegalArgumentException("La descripción del problema es obligatoria");
        }
        if (repair.getClient() == null) {
            throw new IllegalArgumentException("La reparación debe tener un cliente");
        }
        if (repair.getEquipment() == null) {
            throw new IllegalArgumentException("La reparación debe tener un equipo");
        }
        if (repair.getCost() != null && repair.getCost().signum() < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo");
        }
    }
}

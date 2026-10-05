package org.example.crmdesktop.service;

import org.example.crmdesktop.model.Equipment;
import org.example.crmdesktop.repository.EquipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Equipment> findByClientId(Long clientId) {
        return equipmentRepository.findByClientIdOrderByBrandAsc(clientId);
    }

    @Transactional(readOnly = true)
    public List<Equipment> searchBySerialNumber(String serialNumber) {
        if (serialNumber == null || serialNumber.isBlank()) {
            return findAll();
        }
        return equipmentRepository.findBySerialNumberContainingIgnoreCaseOrderByBrandAsc(serialNumber.trim());
    }

    @Transactional
    public Equipment save(Equipment equipment) {
        validate(equipment);
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe el equipo con id " + id);
        }
        equipmentRepository.deleteById(id);
    }

    private void validate(Equipment equipment) {
        if (equipment == null || equipment.getType() == null || equipment.getType().isBlank()) {
            throw new IllegalArgumentException("El tipo de equipo es obligatorio");
        }
        if (equipment.getClient() == null) {
            throw new IllegalArgumentException("El equipo debe estar asociado a un cliente");
        }
    }
}

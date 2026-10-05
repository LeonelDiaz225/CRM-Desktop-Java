package org.example.crmdesktop.service;

import org.example.crmdesktop.model.Client;
import org.example.crmdesktop.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional(readOnly = true)
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Client> searchByName(String name) {
        if (name == null || name.isBlank()) {
            return findAll();
        }
        return clientRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name.trim());
    }

    @Transactional(readOnly = true)
    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el cliente con id " + id));
    }

    @Transactional
    public Client save(Client client) {
        validate(client);
        return clientRepository.save(client);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe el cliente con id " + id);
        }
        clientRepository.deleteById(id);
    }

    private void validate(Client client) {
        if (client == null || client.getName() == null || client.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
    }
}

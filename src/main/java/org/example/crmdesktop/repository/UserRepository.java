package org.example.crmdesktop.repository;

import org.example.crmdesktop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

package com.agendastyle.backend.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonServiceRepository
        extends JpaRepository<SalonService, Long> {

}
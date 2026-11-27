package com.ledgepay.catalogo.infrastructure.persistence.jpa.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.OrganizadorEntity;

public interface OrganizadorJpaRepository extends JpaRepository<OrganizadorEntity, UUID> {
}

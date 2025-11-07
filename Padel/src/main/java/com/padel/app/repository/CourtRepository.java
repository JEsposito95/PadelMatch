package com.padel.app.repository;

import com.padel.app.model.Court;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourtRepository extends JpaRepository<Court,Long> {
    List<Court> findByOwner_IdUser(Long idUser);
}

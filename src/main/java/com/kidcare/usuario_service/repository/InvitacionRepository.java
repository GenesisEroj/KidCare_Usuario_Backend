package com.kidcare.usuario_service.repository;

import com.kidcare.usuario_service.model.Invitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitacionRepository extends JpaRepository<Invitacion, Integer> {
    Optional<Invitacion> findByToken(String token);
    Optional<Invitacion> findByEmailInvitadoAndIdMenorAndUtilizadoFalse(String email, Integer idMenor);
    List<Invitacion> findByIdTutor(Integer idTutor);
    List<Invitacion> findByIdMenorIn(List<Integer> ids);
}

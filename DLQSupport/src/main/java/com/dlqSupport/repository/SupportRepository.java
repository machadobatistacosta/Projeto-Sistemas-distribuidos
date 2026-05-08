package com.dlqSupport.repository;

import com.dlqSupport.entities.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportRepository extends JpaRepository<Mensagem, Long> {
    Mensagem findByMensagemOriginal(String mensagemOriginal);
}

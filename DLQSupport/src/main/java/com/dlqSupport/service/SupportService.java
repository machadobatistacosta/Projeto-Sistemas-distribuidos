package com.dlqSupport.service;
import com.dlqSupport.dto.DLQSupportDTO;
import com.dlqSupport.dto.FindMessageDTO;
import com.dlqSupport.dto.FixedMessageDTO;
import com.dlqSupport.dto.RecoveryMessageDTO;
import com.dlqSupport.entities.Mensagem;
import com.dlqSupport.entities.RegistroEstruturas;
import com.dlqSupport.exception.MensagemNotFoundException;
import com.dlqSupport.producer.MensagemProducer;
import com.dlqSupport.repository.SupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupportService {
    @Autowired
    private MensagemProducer mensagemProducer;

    @Autowired
    private SupportRepository supportRepository;

    public SupportService(SupportRepository supportRepository) {
        this.supportRepository = supportRepository;
    }

    public void processarMensagem(DLQSupportDTO dlqSupportDTO) {
        salvarMensagem(dlqSupportDTO);
        iniciarEditor(getIdByMensagem(dlqSupportDTO.mensagemOriginal()));
    }

    private void salvarMensagem(DLQSupportDTO dlqSupportDTO) {
        Mensagem novaMensagem = new Mensagem();
        novaMensagem.setTipoMensagem(dlqSupportDTO.tipoMensagem());
        novaMensagem.setMensagemOriginal(dlqSupportDTO.mensagemOriginal());
        novaMensagem.setMensagemDeErro(dlqSupportDTO.mensagemDeErro());
        novaMensagem.setTipoErro(dlqSupportDTO.tipoErro());
        novaMensagem.setTimestamp(dlqSupportDTO.timestamp());
        novaMensagem.setFilaDeOrigem(dlqSupportDTO.filaDeOrigem());
        supportRepository.save(novaMensagem);
    }

    public void iniciarEditor(Long id) {
        System.out.println("\nACESSO AO EDITOR JSON: http://localhost:9091/mensagem/editor/" + id);
    }

    public void atualizarMensagem(FixedMessageDTO fixedMensagemDto) {
        Mensagem mensagemAtualizada = supportRepository.findById(fixedMensagemDto.id()).orElseThrow(() -> new MensagemNotFoundException("Não foi possível encontrar a mensagem"));
        System.out.println(fixedMensagemDto);
        mensagemAtualizada.setCorrecaoDocumentada(fixedMensagemDto.correcaoDocumentada());
        mensagemAtualizada.setMensagemCorrigida(fixedMensagemDto.mensagemCorrigida());
        supportRepository.save(mensagemAtualizada);
        mensagemProducer.reenviarJson(fixedMensagemDto);
    }


    public FindMessageDTO getMensagemById(Long id) {
        Mensagem mensagemDesejada = supportRepository.findById(id).orElseThrow(() -> new MensagemNotFoundException("Não foi encontrada mensagem com id" + id));
        RegistroEstruturas registroEstruturas = new RegistroEstruturas();
        System.out.println(mensagemDesejada);
        return new FindMessageDTO(
                mensagemDesejada.getMensagemOriginal(),
                mensagemDesejada.getTipoMensagem(),
                mensagemDesejada.getMensagemDeErro(),
                mensagemDesejada.getFilaDeOrigem(),
                mensagemDesejada.getTimestamp(),
                registroEstruturas.buscarRegistro(
                        mensagemDesejada.getTipoMensagem()),
                mensagemDesejada.getTipoErro());
    }

    public Long getIdByMensagem(String mensagem) {
        return supportRepository.findByMensagemOriginal(mensagem).getId();
    }

    public List<RecoveryMessageDTO> exibirMensagens() {
        return supportRepository.findAll().stream().map(m ->
                new RecoveryMessageDTO(
                        m.getId(),
                        m.getMensagemOriginal(),
                        m.getTipoMensagem(),
                        m.getFilaDeOrigem(), 
                        m.getTipoErro(),
                        m.getMensagemDeErro(),
                        m.getMensagemCorrigida(),
                        m.getCorrecaoDocumentada(),
                        m.getTimestamp()))
                .toList();
    }
}

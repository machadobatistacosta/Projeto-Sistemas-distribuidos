package com.dlqSupport.controller;

import com.dlqSupport.dto.DLQSupportDTO;
import com.dlqSupport.dto.FindMessageDTO;
import com.dlqSupport.dto.FixedMessageDTO;
import com.dlqSupport.dto.RecoveryMessageDTO;
import com.dlqSupport.entities.CamposExigidos;
import com.dlqSupport.entities.EstruturaMensagem;
import com.dlqSupport.exception.MensagemNotFoundException;
import com.dlqSupport.service.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mensagem")
public class MensagemController {

    @Autowired
    private SupportService supportService;

    public MensagemController(SupportService supportService) {
        this.supportService = supportService;
    }

    @PatchMapping("/reenviar")
    public ResponseEntity<String> reenviarMensagem(@RequestBody FixedMessageDTO fixedMensagemDto) {
        try {
            supportService.atualizarMensagem(fixedMensagemDto);
            return ResponseEntity.ok("Recebido com sucesso!");
        } catch (MensagemNotFoundException e) {
            System.out.println("F");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage() + ", log: " + e);
        }
    }

    @GetMapping("/obter/{id}")
    public ResponseEntity<FindMessageDTO> buscarMensagem(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(supportService.getMensagemById(id));
        } catch (MensagemNotFoundException e) {
            System.out.println("necessário inserir exceção aqui");
            return ResponseEntity.ok(new FindMessageDTO("", ".", ".", ".", LocalDateTime.now(), new EstruturaMensagem(Map.of("", new CamposExigidos("", false, null, null))), ""));
        }
    }

    @GetMapping("/editor/{id}")
    public ModelAndView editorJson() {
        return new ModelAndView("editor");
    }

    @GetMapping("/obter")
    public ResponseEntity<List<RecoveryMessageDTO>> exibirTodasMensagens() {
        return ResponseEntity.ok(supportService.exibirMensagens());
    }
}

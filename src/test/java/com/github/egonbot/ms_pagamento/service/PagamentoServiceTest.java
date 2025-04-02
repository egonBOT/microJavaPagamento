package com.github.egonbot.ms_pagamento.service;

import com.github.egonbot.ms_pagamento.dto.PagamentoDTO;
import com.github.egonbot.ms_pagamento.entity.Pagamento;
import com.github.egonbot.ms_pagamento.repository.PagamentoRepository;
import com.github.egonbot.ms_pagamento.service.exceptions.ResourceNotFoundException;
import com.github.egonbot.ms_pagamento.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class PagamentoServiceTest {

    @InjectMocks
    private PagamentoService service;

    @Mock
    private PagamentoRepository repository;

    private Long existingId;
    private Long nonExistingId;

    private Pagamento pagamento;
    private PagamentoDTO dto;

    @BeforeEach
    void setuo() throws Exception {
        existingId = 1l;
        nonExistingId = 10L;

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.doNothing().when(repository).deleteById(existingId);

        pagamento = Factory.createPagamento();
        dto = new PagamentoDTO(pagamento);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(pagamento));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        Mockito.when(repository.save(any())).thenReturn(pagamento);
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(pagamento);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

    }

    @Test
    @DisplayName("delete deveria nao fazer nada quando o Id existe")
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(
                () -> {
                    service.deletePagamento(existingId);
                }
        );
    }

    @Test
    @DisplayName("delete deveria lançar excecao ResourceNotfoundExcepiton quando o Id nao existe")
    public void deleteShouldThrowResourceNotExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> {
                    service.deletePagamento(nonExistingId);
                }
        );
    }

    @Test
    public void getByShouldReturnPagamentoDTOWhenIdExists() {
        PagamentoDTO dto = service.getById(existingId);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(dto.getId(), existingId);
        Assertions.assertEquals(dto.getValor(), pagamento.getValor());

    }

    @Test
    public void getByShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.getById(nonExistingId);
        });
    }

    @Test
    public void createPagamentoShouldReturnPagamentoDTOWhenPagamentoIsCreated() {
        PagamentoDTO dto = service.createPagamento(this.dto);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(dto.getId(), pagamento.getId());
    }

    @Test
    public void updatePagamentoShouldReturnPagamentoDTOWhenIdExists() {
        PagamentoDTO dto = service.updatePagamento(pagamento.getId(), this.dto);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(dto.getId(), existingId);
        Assertions.assertEquals(dto.getValor(), pagamento.getValor());

    }

    @Test
    public  void updatePagamentoShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            service.updatePagamento(nonExistingId, dto);
        });
    }
}

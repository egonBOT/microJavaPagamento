package com.github.egonbot.ms_pagamento.service;

import com.github.egonbot.ms_pagamento.repository.PagamentoRepository;
import com.github.egonbot.ms_pagamento.service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class PagamentoServiceTestIT {

    @Autowired
    private PagamentoService service;
    @Autowired
    private PagamentoRepository repository;
    private Long existingId;
    private Long nonExistingId;
    private Long contTotalPagamentos;

    @BeforeEach
    void setup() throws Exception{
        existingId = 1L;
        nonExistingId = 100L;
        contTotalPagamentos= 6L;
    }

    @Test
    public void deletePagamentoShouldDeleteResourceWhenIdExists(){
        service.deletePagamento(existingId);
        Assertions.assertEquals(contTotalPagamentos-1, repository.count());
    }

    @Test
    public void delePagamentoShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class,() ->{
            service.deletePagamento(nonExistingId);
        });
    }
}

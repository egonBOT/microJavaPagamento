package com.github.egonbot.ms_pagamento.repository;

import com.github.egonbot.ms_pagamento.entity.Pagamento;
import com.github.egonbot.ms_pagamento.service.PagamentoService;
import com.github.egonbot.ms_pagamento.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
@DataJpaTest
public class PagamentoRepositoryTests {


    @Autowired
    private PagamentoRepository repository;

        //declarando variáveis
        private Long existingId;
        private Long nonExistingId;
        private Long countTotalPagamento;

        @BeforeEach
        void setup() throws Exception {
            existingId = 1L;
            nonExistingId = 100L;
            countTotalPagamento = 3L;

        }

        @Test
        public void deleteShouldDeleteObjectWhenIdExists() {
            Long existingId = 1L;
            //Act
            repository.deleteById(existingId);
            //Assert
            Optional<Pagamento> result = repository.findById(existingId);
            Assertions.assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Dado parâmetros válidos e Id nulo, quando chamar Criar Pagamento então deve instanciar um Pagamento")
        public void givenValidParamsAndIdIsNull_whenCallCreatePagamento_thenInstantieAPagamento() {


            Pagamento pagamento = Factory.createPagamento();
            pagamento.setId(null);
            pagamento = repository.save(pagamento);
            Assertions.assertNotNull(pagamento.getId());
            Assertions.assertEquals(countTotalPagamento + 1, pagamento.getId());
        }

        @Test
        @DisplayName("given a non-existing ID when calling findById then it should return an empty Optional")
        public void givenAnExistingId_whenCallFindById_thenReturnNonEmptyOptional() {

            Optional<Pagamento> result = repository.findById(existingId);
            Assertions.assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("dado um ID não existente quando chamar findById então deve retornar um Optional vazio")
        public void givenANonExistingId_whenCallFindById_thenReturnAnEmptyOptional() {

            Optional<Pagamento> result = repository.findById(nonExistingId);
            Assertions.assertFalse(result.isPresent());
            //ou
            Assertions.assertTrue(result.isEmpty());
        }


    }

package com.github.egonbot.ms_pagamento.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.egonbot.ms_pagamento.dto.PagamentoDTO;
import com.github.egonbot.ms_pagamento.service.PagamentoService;
import com.github.egonbot.ms_pagamento.service.exceptions.ResourceNotFoundException;
import com.github.egonbot.ms_pagamento.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagamentoService service;

    private PagamentoDTO dto;
    private Long existingId;
    private Long nonExistingId;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws Exception {

        existingId = 1L;
        nonExistingId = 100L;

        dto = Factory.createPagamentoDTO();

        List<PagamentoDTO> list = List.of(dto);

        // simulando o comportamento do getALL
        Mockito.when(service.getAll()).thenReturn(list);

        // simulando o comportamento do getByID - service
        // id existe
        Mockito.when(service.getById(existingId)).thenReturn(dto);
        // id não existe - lança exception
        Mockito.when(service.getById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        // simulando o comportamento do service - createPagamento

        Mockito.when(service.createPagamento(any())).thenReturn(dto);

        Mockito.when(service.updatePagamento(eq(existingId), any())).thenReturn(dto);

        Mockito.when(service.updatePagamento(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
        //delete metodo invertido
        Mockito.doNothing().when(service).deletePagamento(existingId);

        Mockito.doThrow(ResourceNotFoundException.class).when(service).deletePagamento(nonExistingId);
    }

    @Test
    public void getAllShouldReturnListPagamentoDTO() throws Exception {

        //chamando a requisição com o método GET - endpoint /pagamentos
        ResultActions result = mockMvc.perform(get("/pagamentos").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
    }

    @Test
    public void getByIdShouldReturnPagamentoDTOWhenIdExists() throws Exception {

        ResultActions result = mockMvc.perform(get("/pagamentos/{id}", existingId).accept(MediaType.APPLICATION_JSON));

        //Assertions
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.valor").exists());
        result.andExpect(jsonPath("$.status").exists());

    }

    @Test
    public void getByIdShouldTrhowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {

        ResultActions result = mockMvc.perform(get("/pagamentos/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void createPagamentoShouldReturnPagamentoDTOCreated() throws Exception {

        PagamentoDTO newPagamentoDTO = Factory.createNewPagamentoDTO();

        String jsonRequestBody = objectMapper.writeValueAsString(newPagamentoDTO);

        mockMvc.perform(post("/pagamentos").content(jsonRequestBody).contentType(MediaType.APPLICATION_JSON) // request
                        .accept(MediaType.APPLICATION_JSON)) // response

                .andDo(print()).andExpect(status().isCreated()).andExpect(header().exists("Location")).andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.valor").exists()).andExpect(jsonPath("$.valor").value(32.25)).andExpect(jsonPath("$.status").exists()).andExpect(jsonPath("$.pedidoId").exists()).andExpect(jsonPath("$.formaDePagamentoId").exists());

    }

    @Test
    public void updatePagamentoShouldReturnPagamentoDTOWhenIdExists() throws Exception {
        String jsonRequestBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/pagamentos/{id}", existingId).content(jsonRequestBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk()).andExpect(header().exists("Location")).andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.valor").exists()).andExpect(jsonPath("$.status").exists()).andExpect(jsonPath("$.pedidoId").exists()).andExpect(jsonPath("$.formaDePagamentoId").exists());

    }

    @Test
    public void UpdatePagamentoShouldReturnNotFoundExceptionWhenIdDoesNotExist() throws Exception {
        String jsonRequestBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/pagamentos/{id}", nonExistingId).content(jsonRequestBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("deletePagamento devera nao fazer nada quando o id existe")
    public void deletePagamentoShouldDoNothingWhenIdExists() throws Exception {
        mockMvc.perform(delete("/pagamentos/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void deletePagamentoShouldReturnNotFoundExceptionWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/pagamentos/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}

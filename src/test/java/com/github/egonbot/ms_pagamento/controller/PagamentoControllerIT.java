package com.github.egonbot.ms_pagamento.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.egonbot.ms_pagamento.dto.PagamentoDTO;
import com.github.egonbot.ms_pagamento.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PagamentoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private Long existingId;
    private Long nonExistingId;
    private PagamentoDTO pagamentoDTO;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws Exception {
        existingId = 1L;
        nonExistingId = 50L;
        pagamentoDTO = Factory.createPagamentoDTO();
    }

    @Test
    public void getAllShouldReturnListAllPagamentos() throws Exception {

        mockMvc.perform(get("/pagamentos")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value(1L))
                .andExpect(jsonPath("[0].nome").isString())
                .andExpect(jsonPath("[0].nome").value("Amadeus Mozart"))
                .andExpect(jsonPath("[5].status").value("CONFIRMADO"));
    }

    @Test
    public void getByIdShouldReturnPagamentoDTOWhenIdExists() throws Exception {

        mockMvc.perform(get("/pagamentos/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("nome").isString())
                .andExpect(jsonPath("nome").value("Amadeus Mozart"))
                .andExpect(jsonPath("status").value("CRIADO"));
    }

    @Test
    public void getByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {

        mockMvc.perform(get("/pagamentos/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void createShouldReturnPagamentoDTO() throws Exception {

        pagamentoDTO = Factory.createNewPagamentoDTO();
        String jsonRequestBody = objectMapper.writeValueAsString(pagamentoDTO);

        mockMvc.perform(post("/pagamentos")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.nome").value(pagamentoDTO.getNome()))
                .andExpect(jsonPath("$.status").value("CRIADO"));

    }
    @Test
    public void createShouldPersistPagamentoWithRequiredFields() throws Exception {

        pagamentoDTO = Factory.createNewPagamentoDTOWithRequiredFields();
        String jsonRequestBody = objectMapper.writeValueAsString(pagamentoDTO);
        mockMvc.perform(post("/pagamentos")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.valor").value(pagamentoDTO.getValor()))
                .andExpect(jsonPath("$.status").value("CRIADO"))
                .andExpect(jsonPath("$.nome").isEmpty())
                .andExpect(jsonPath("$.validade").isEmpty());
    }

    @Test
    @DisplayName("create  deve lançar  expetion quando os dados estiverem errados")
    public void createShouldThrowsExceptionWhenInvalidData() throws Exception{

        pagamentoDTO = Factory.createNewPagamentoDTOWithInvalidData();
        String jsonRequestBody = objectMapper.writeValueAsString(pagamentoDTO);
        mockMvc.perform(post("/pagamentos")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

    }

    @Test
    public void updateShouldUpdateAndReturnPagamentoDTOWhenIdExists() throws Exception{

        String jsonBody= objectMapper.writeValueAsString(pagamentoDTO);
        mockMvc.perform(put("/pagamentos/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.valor").value(pagamentoDTO.getValor()))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value("CRIADO"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists()throws Exception{

        String jsonRequestBody = objectMapper.writeValueAsString(pagamentoDTO);
        mockMvc.perform(post("/pagamentos", nonExistingId)
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    public void updateShouldThrowsExceptionWhenInvalidDate() throws Exception{

        pagamentoDTO= Factory.createNewPagamentoDTOWithInvalidData();

        String jsonRequestBody = objectMapper.writeValueAsString(pagamentoDTO);
        mockMvc.perform(post("/pagamentos", existingId)
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

    }
    @Test
    public void  deleteShouldReturnNoContentWhenIdExists() throws Exception{
        mockMvc.perform(delete("/pagamentos/{id}",existingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception{
        mockMvc.perform(delete("/pagamentos/{id}",nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

    }


}

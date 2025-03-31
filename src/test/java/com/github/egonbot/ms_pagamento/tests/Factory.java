package com.github.egonbot.ms_pagamento.tests;

import com.github.egonbot.ms_pagamento.entity.Pagamento;
import com.github.egonbot.ms_pagamento.entity.Status;

import java.math.BigDecimal;

public class Factory {

    public static Pagamento createPagamento() {
        Pagamento pagamento = new Pagamento(1L, BigDecimal.valueOf(32.25), " egon", "51555028888", "21/03", "585", Status.CRIADO, 1L, 2L);
        return pagamento;
    }
}

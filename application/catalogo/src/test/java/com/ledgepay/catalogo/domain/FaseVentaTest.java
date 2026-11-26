package com.ledgepay.catalogo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.ledgepay.catalogo.domain.value_objects.Fase;

class FaseVentaTest {

    @Test
    void iniciarVentaGeneral_debeCambiarFaseAGeneral() {
        FaseVenta faseVenta = new FaseVenta();

        faseVenta.iniciarVentaGeneral();

        assertThat(faseVenta.getFase()).isEqualTo(Fase.GENERAL);
    }
}

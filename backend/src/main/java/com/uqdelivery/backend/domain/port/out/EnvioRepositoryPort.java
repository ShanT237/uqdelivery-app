package com.uqdelivery.backend.domain.port.out;

import com.uqdelivery.backend.domain.model.Envio;

/**
 * Puerto de salida para la persistencia de Envio.
 */
public interface EnvioRepositoryPort {

    Envio save(Envio envio);
}

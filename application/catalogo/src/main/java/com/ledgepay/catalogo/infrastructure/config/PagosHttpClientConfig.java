package com.ledgepay.catalogo.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

import com.ledgepay.catalogo.infrastructure.http.PagosHttpInterface;

@Configuration(proxyBeanMethods = false)
@ImportHttpServices(group = "pagos", types = PagosHttpInterface.class)
public class PagosHttpClientConfig {
}

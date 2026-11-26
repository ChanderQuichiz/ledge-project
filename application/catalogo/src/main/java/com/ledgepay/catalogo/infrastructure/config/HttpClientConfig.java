package com.ledgepay.catalogo.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

import com.ledgepay.catalogo.infrastructure.http.EntradasHttpInterface;

@Configuration(proxyBeanMethods = false)
@ImportHttpServices(group = "entradas", types = EntradasHttpInterface.class)
public class HttpClientConfig {
}

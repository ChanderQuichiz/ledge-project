package com.ledgepay.catalogo.application.port.out;

import com.ledgepay.catalogo.application.query.EntradasByEventoQuery;
import com.ledgepay.catalogo.application.result.EntradasByEventoResult;

public interface EntradasClient {
    EntradasByEventoResult getEntradasByEvento(EntradasByEventoQuery query);
}

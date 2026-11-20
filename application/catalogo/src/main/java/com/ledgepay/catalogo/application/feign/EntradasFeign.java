package com.ledgepay.catalogo.application.feign;

import com.ledgepay.catalogo.application.query.EntradasByEventoQuery;
import com.ledgepay.catalogo.application.result.EntradasByEventoResult;

public interface EntradasFeign {
    EntradasByEventoResult getEntradasByEvento(EntradasByEventoQuery query);
}

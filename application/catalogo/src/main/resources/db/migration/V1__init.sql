CREATE TABLE IF NOT EXISTS organizadores (
    id UUID PRIMARY KEY,
    ruc VARCHAR(11) NOT NULL UNIQUE,
    razon_social VARCHAR(200) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE_VERIFICACION',
    porcentaje_comision DECIMAL(4,2) NOT NULL,
    verificado_por UUID,
    verificado_en TIMESTAMP,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_organizadores_comision CHECK (porcentaje_comision BETWEEN 3 AND 15),
    CONSTRAINT ck_organizadores_ruc CHECK (ruc ~ '^[0-9]{11}$')
);

CREATE TABLE IF NOT EXISTS sedes (
    id UUID PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(250) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    aforo_maximo_legal INTEGER NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_sedes_aforo CHECK (aforo_maximo_legal > 0)
);

CREATE INDEX IF NOT EXISTS ix_sedes_ciudad ON sedes (ciudad);

CREATE TABLE IF NOT EXISTS zonas_sede (
    id UUID PRIMARY KEY,
    sede_id UUID NOT NULL REFERENCES sedes (id),
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    capacidad INTEGER NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_zonas_sede_sede_nombre UNIQUE (sede_id, nombre),
    CONSTRAINT ck_zonas_capacidad CHECK (capacidad > 0)
);

CREATE TABLE IF NOT EXISTS asientos_zona (
    id UUID PRIMARY KEY,
    zona_id UUID NOT NULL REFERENCES zonas_sede (id),
    fila VARCHAR(10) NOT NULL,
    numero INTEGER NOT NULL,
    accesible BOOLEAN NOT NULL DEFAULT FALSE,
    habilitado BOOLEAN NOT NULL DEFAULT TRUE,
    motivo_inhabilitacion VARCHAR(40),
    CONSTRAINT uk_asientos_zona_zona_fila_numero UNIQUE (zona_id, fila, numero)
);

CREATE TABLE IF NOT EXISTS puntos_acceso_sede (
    id UUID PRIMARY KEY,
    sede_id UUID NOT NULL REFERENCES sedes (id),
    nombre VARCHAR(100) NOT NULL,
    CONSTRAINT uk_puntos_acceso_sede_sede_nombre UNIQUE (sede_id, nombre)
);

CREATE TABLE IF NOT EXISTS puntos_acceso_zonas (
    punto_acceso_id UUID NOT NULL REFERENCES puntos_acceso_sede (id),
    zona_id UUID NOT NULL REFERENCES zonas_sede (id),
    PRIMARY KEY (punto_acceso_id, zona_id)
);

CREATE TABLE IF NOT EXISTS eventos (
    id UUID PRIMARY KEY,
    organizador_id UUID NOT NULL REFERENCES organizadores (id),
    sede_id UUID NOT NULL REFERENCES sedes (id),
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    categoria VARCHAR(60) NOT NULL,
    restriccion_edad VARCHAR(20) NOT NULL DEFAULT 'TODO_PUBLICO',
    politica_cancelacion VARCHAR(30) NOT NULL,
    permite_reingreso BOOLEAN NOT NULL DEFAULT FALSE,
    max_reingresos INTEGER,
    es_nominativo BOOLEAN NOT NULL DEFAULT FALSE,
    es_exonerado BOOLEAN NOT NULL DEFAULT FALSE,
    limite_compra_por_cliente INTEGER NOT NULL DEFAULT 6,
    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    apertura_puertas TIMESTAMP NOT NULL,
    inicio_venta TIMESTAMP,
    fin_venta TIMESTAMP,
    cargo_servicio_porcentaje DECIMAL(5,2) NOT NULL DEFAULT 10.00,
    cargo_servicio_minimo DECIMAL(12,2) NOT NULL DEFAULT 2.00,
    cargo_servicio_maximo DECIMAL(12,2) NOT NULL DEFAULT 15.00,
    porcentaje_comision DECIMAL(4,2),
    numero_reprogramaciones INTEGER NOT NULL DEFAULT 0,
    publicado_en TIMESTAMP,
    cancelado_en TIMESTAMP,
    motivo_cancelacion TEXT,
    penalizacion_cancelacion_porcentaje DECIMAL(4,2) NOT NULL DEFAULT 0,
    creado_por UUID NOT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_eventos_fechas CHECK (fecha_fin > fecha_inicio),
    CONSTRAINT ck_eventos_apertura CHECK (apertura_puertas <= fecha_inicio),
    CONSTRAINT ck_eventos_limite CHECK (limite_compra_por_cliente BETWEEN 1 AND 10),
    CONSTRAINT ck_eventos_reprogramaciones CHECK (numero_reprogramaciones BETWEEN 0 AND 2),
    CONSTRAINT ck_eventos_comision CHECK (porcentaje_comision IS NULL OR porcentaje_comision BETWEEN 3 AND 15),
    CONSTRAINT ck_eventos_penalizacion CHECK (penalizacion_cancelacion_porcentaje BETWEEN 0 AND 5),
    CONSTRAINT ck_eventos_reingreso CHECK ((permite_reingreso AND max_reingresos >= 1) OR (NOT permite_reingreso AND max_reingresos IS NULL))
);

CREATE INDEX IF NOT EXISTS ix_eventos_sede_horario ON eventos (sede_id, fecha_inicio, fecha_fin);
CREATE INDEX IF NOT EXISTS ix_eventos_organizador_estado ON eventos (organizador_id, estado);

CREATE TABLE IF NOT EXISTS fases_venta (
    id UUID PRIMARY KEY,
    evento_id UUID NOT NULL REFERENCES eventos (id),
    fase VARCHAR(20) NOT NULL,
    inicia_en TIMESTAMP NOT NULL,
    termina_en TIMESTAMP NOT NULL,
    acceso_restringido BOOLEAN NOT NULL DEFAULT FALSE,
    codigo_acceso_hash VARCHAR(128),
    CONSTRAINT uk_fases_venta_evento_fase UNIQUE (evento_id, fase),
    CONSTRAINT ck_fases_rango CHECK (termina_en > inicia_en),
    CONSTRAINT ck_fases_acceso CHECK ((NOT acceso_restringido) OR (fase = 'PREVENTA' AND codigo_acceso_hash IS NOT NULL))
);

CREATE INDEX IF NOT EXISTS ix_fases_venta_evento_fase ON fases_venta (evento_id, fase);

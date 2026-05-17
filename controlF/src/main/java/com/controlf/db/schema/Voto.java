package com.controlf.db.schema;

import java.util.UUID;

import com.controlf.db.schema.enums.TipoVoto;

import java.time.LocalDateTime;

public class Voto {

    private UUID id;
    private UUID politicoId;
    private UUID leyId;
    private TipoVoto tipoVoto;
    private Boolean asistencia;
    private LocalDateTime fechaVoto;

    /* 
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPoliticoId() { return politicoId; }
    public void setPoliticoId(UUID politicoId) { this.politicoId = politicoId; }

    public UUID getLeyId() { return leyId; }
    public void setLeyId(UUID leyId) { this.leyId = leyId; }

    public TipoVoto getTipoVoto() {
    return tipoVoto;
    }

    public void setTipoVoto(TipoVoto tipoVoto) {
    this.tipoVoto = tipoVoto;
    }

    public Boolean getAsistencia() { return asistencia; }
    public void setAsistencia(Boolean asistencia) { this.asistencia = asistencia; }

    public LocalDateTime getFechaVoto() { return fechaVoto; }
    public void setFechaVoto(LocalDateTime fechaVoto) { this.fechaVoto = fechaVoto; }
    */
    
}
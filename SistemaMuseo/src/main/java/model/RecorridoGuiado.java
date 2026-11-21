package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tour")
public class RecorridoGuiado extends EntidadCultural {
    @Column(name = "start_at")
    private LocalDateTime fechaInicio;

    @Column(name = "end_at")
    private LocalDateTime fechaFin;

    @Column(name = "landing_page_url", length = 255)
    private String urlPagina;

    public RecorridoGuiado() {}

    @Override
    public String obtenerResumen() {
        return "Recorrido: " + getNombre();
    }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
    public String getUrlPagina() { return urlPagina; }
    public void setUrlPagina(String urlPagina) { this.urlPagina = urlPagina; }
}
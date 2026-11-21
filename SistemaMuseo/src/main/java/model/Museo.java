package model;

import jakarta.persistence.*;

@Entity
@Table(name = "museum")
public class Museo extends EntidadCultural {
    @Column(name = "latitude")
    private Double latitud;

    @Column(name = "longitude")
    private Double longitud;

    @Column(name = "landing_page_url", length = 255)
    private String urlPagina;

    public Museo() {}

    @Override
    public String obtenerResumen() {
        return "Museo: " + getNombre() + " (" + latitud + ", " + longitud + ")";
    }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public String getUrlPagina() { return urlPagina; }
    public void setUrlPagina(String urlPagina) { this.urlPagina = urlPagina; }
}
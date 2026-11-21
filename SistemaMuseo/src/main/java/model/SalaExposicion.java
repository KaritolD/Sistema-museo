package model;

import jakarta.persistence.*;

@Entity
@Table(name = "exhibition_room")
public class SalaExposicion extends EntidadCultural {
    @Column(name = "capacity")
    private Integer capacidad;

    @ManyToOne
    @JoinColumn(name = "museum_id", nullable = false)
    private Museo museo;

    public SalaExposicion() {}

    @Override
    public String obtenerResumen() {
        return "Sala: " + getNombre() + " (Capacidad: " + capacidad + ")";
    }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    public Museo getMuseo() { return museo; }
    public void setMuseo(Museo museo) { this.museo = museo; }
}
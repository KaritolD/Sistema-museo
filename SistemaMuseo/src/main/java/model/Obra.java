package model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "artifact")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_obra", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipoObra", defaultImpl = Obra.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Pintura.class, name = "PINTURA"),
        @JsonSubTypes.Type(value = Escultura.class, name = "ESCULTURA")
})
public class Obra extends EntidadCultural {
    @Column(name = "image_url", length = 255)
    private String imagenUrl;

    @Column(name = "created_at")
    private LocalDateTime fechaCreacion;

    @Column(name = "acquired_at")
    private LocalDateTime fechaAdquisicion;

    @Column(name = "condition_percentage")
    private Integer porcentajeConservacion;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artista artista;

    @ManyToOne
    @JoinColumn(name = "exhibition_room_id")
    private SalaExposicion salaActual;

    public Obra() {}

    public String evaluarConservacion() {
        return (porcentajeConservacion != null && porcentajeConservacion >= 75)
                ? "Estado óptimo (" + porcentajeConservacion + "%)"
                : "Requiere revisión general (" + porcentajeConservacion + "%)";
    }

    @Override
    public String obtenerResumen() {
        return getNombre() + " - " + evaluarConservacion();
    }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaAdquisicion() { return fechaAdquisicion; }
    public void setFechaAdquisicion(LocalDateTime fechaAdquisicion) { this.fechaAdquisicion = fechaAdquisicion; }
    public Integer getPorcentajeConservacion() { return porcentajeConservacion; }
    public void setPorcentajeConservacion(Integer porcentajeConservacion) { this.porcentajeConservacion = porcentajeConservacion; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Artista getArtista() { return artista; }
    public void setArtista(Artista artista) { this.artista = artista; }
    public SalaExposicion getSalaActual() { return salaActual; }
    public void setSalaActual(SalaExposicion salaActual) { this.salaActual = salaActual; }
}
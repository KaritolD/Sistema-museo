package model;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class EntidadCultural {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String nombre;

    @Column(name = "description", columnDefinition = "TEXT")
    private String descripcion;

    public EntidadCultural() {}

    public EntidadCultural(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public abstract String obtenerResumen();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
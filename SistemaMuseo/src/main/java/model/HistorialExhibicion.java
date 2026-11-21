package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "exhibited_in")
public class HistorialExhibicion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "artifact_id", nullable = false)
    private Obra obra;

    @ManyToOne
    @JoinColumn(name = "exhibition_room_id", nullable = false)
    private SalaExposicion sala;

    @Column(name = "start_date", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "end_date")
    private LocalDate fechaFin;

    public HistorialExhibicion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Obra getObra() { return obra; }
    public void setObra(Obra obra) { this.obra = obra; }

    public SalaExposicion getSala() { return sala; }
    public void setSala(SalaExposicion sala) { this.sala = sala; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
}
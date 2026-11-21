package model;

import jakarta.persistence.*;

@Entity
@Table(name = "tour_room")
public class RecorridoSala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private RecorridoGuiado recorrido;

    @ManyToOne
    @JoinColumn(name = "exhibition_room_id", nullable = false)
    private SalaExposicion sala;

    @Column(name = "sequence_number", nullable = false)
    private Integer ordenSecuencia;

    public RecorridoSala() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RecorridoGuiado getRecorrido() { return recorrido; }
    public void setRecorrido(RecorridoGuiado recorrido) { this.recorrido = recorrido; }

    public SalaExposicion getSala() { return sala; }
    public void setSala(SalaExposicion sala) { this.sala = sala; }

    public Integer getOrdenSecuencia() { return ordenSecuencia; }
    public void setOrdenSecuencia(Integer ordenSecuencia) { this.ordenSecuencia = ordenSecuencia; }
}
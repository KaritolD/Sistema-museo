package model;

import jakarta.persistence.*;

@Entity
@Table(name = "category")
public class Categoria extends EntidadCultural {
    public Categoria() {}

    @Override
    public String obtenerResumen() {
        return "Categoría: " + getNombre();
    }
}
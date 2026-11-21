package model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PINTURA")
public class Pintura extends Obra {
    public Pintura() {}

    @Override
    public String evaluarConservacion() {
        if (getPorcentajeConservacion() != null && getPorcentajeConservacion() < 70) {
            return "ALERTA PINTURA (" + getPorcentajeConservacion() + "%): Riesgo de deterioro en lienzo/pigmentos.";
        }
        return "Pintura estable (" + getPorcentajeConservacion() + "%).";
    }
}
package model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ESCULTURA")
public class Escultura extends Obra {
    public Escultura() {}

    @Override
    public String evaluarConservacion() {
        if (getPorcentajeConservacion() != null && getPorcentajeConservacion() < 60) {
            return "ALERTA ESCULTURA (" + getPorcentajeConservacion() + "%): Revisar fisuras estructurales.";
        }
        return "Escultura estable (" + getPorcentajeConservacion() + "%).";
    }
}
package at.uastw.energy.api;

import java.time.Instant;

// This class is created for compatibility with the existing JavaFX client
public class LegacyEnergyData {
    private Instant created;
    private double leistung;

    public LegacyEnergyData() {}

    public LegacyEnergyData(Instant created, double leistung) {
        this.created = created;
        this.leistung = leistung;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public double getLeistung() {
        return leistung;
    }

    public void setLeistung(double leistung) {
        this.leistung = leistung;
    }
} 
package at.uastw.energy.client.model;

import java.time.Instant;

public class EnergyData {
    private Instant created;
    private double leistung;

    public EnergyData() {}

    public EnergyData(Instant created, double leistung) {
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

    @Override
    public String toString() {
        return String.format("EnergyData{created=%s, leistung=%.2f}", created, leistung);
    }
} 
package com.tcc.DoseDaily.Models;

import com.tcc.DoseDaily.Adapters.HistoricAdapter;

public class HistoryItem implements HistoricAdapter.Item {
    private String medicationName;
    private String consumptionTime;
    private int consumptionDay;
    private String diaDaSemana;

    public HistoryItem() {

    }

    public HistoryItem(String medicationName, String consumptionTime, int consumptionDay, String diaDaSemana) {
        this.medicationName = medicationName;
        this.consumptionTime = consumptionTime;
        this.consumptionDay = consumptionDay;
        this.diaDaSemana = diaDaSemana;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getConsumptionTime() {
        return consumptionTime;
    }

    public void setConsumptionTime(String consumptionTime) {
        this.consumptionTime = consumptionTime;
    }

    public int getConsumptionDay() {
        return consumptionDay;
    }

    public void setConsumptionDay(int consumptionDay) {
        this.consumptionDay = consumptionDay;
    }

    public String getDiaDaSemana() {
        return diaDaSemana;
    }

    public void setDiaDaSemana(String diaDaSemana) {
        this.diaDaSemana = diaDaSemana;
    }

    @Override
    public int getItemType() {
        return HistoricAdapter.CONTENT_TYPE;
    }
}

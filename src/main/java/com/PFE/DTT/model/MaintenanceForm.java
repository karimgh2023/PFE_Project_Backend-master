package com.PFE.DTT.model;

import jakarta.persistence.*;

@Entity
public class MaintenanceForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private ControlStandard controlStandard;

    @Enumerated(EnumType.STRING)
    private CurrentType currentType;

    @Enumerated(EnumType.STRING)
    private NetworkForm networkForm;

    private String powerCircuit;
    private String controlCircuit;
    private String fuseValue;

    @Column(nullable = false)
    private Boolean hasTransformer = false;

    private String frequency;
    private String phaseBalanceTest380v;
    private String phaseBalanceTest210v;
    private String insulationResistanceMotor;
    private String insulationResistanceCable;
    private String machineSizeHeight;
    private String machineSizeLength;
    private String machineSizeWidth;

    @Column(nullable = false)
    private Boolean isInOrder  = false;



    @OneToOne
    @JoinColumn(name = "report_id")
    private Report report;

    public MaintenanceForm() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public ControlStandard getControlStandard() { return controlStandard; }
    public void setControlStandard(ControlStandard controlStandard) { this.controlStandard = controlStandard; }

    public CurrentType getCurrentType() { return currentType; }
    public void setCurrentType(CurrentType currentType) { this.currentType = currentType; }

    public NetworkForm getNetworkForm() { return networkForm; }
    public void setNetworkForm(NetworkForm networkForm) { this.networkForm = networkForm; }

    public String getPowerCircuit() { return powerCircuit; }
    public void setPowerCircuit(String powerCircuit) { this.powerCircuit = powerCircuit; }

    public String getControlCircuit() { return controlCircuit; }
    public void setControlCircuit(String controlCircuit) { this.controlCircuit = controlCircuit; }

    public String getFuseValue() { return fuseValue; }
    public void setFuseValue(String fuseValue) { this.fuseValue = fuseValue; }

    public Boolean getHasTransformer() { return hasTransformer; }
    public void setHasTransformer(Boolean hasTransformer) { this.hasTransformer = hasTransformer; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getPhaseBalanceTest380v() { return phaseBalanceTest380v; }
    public void setPhaseBalanceTest380v(String phaseBalanceTest380v) { this.phaseBalanceTest380v = phaseBalanceTest380v; }

    public String getPhaseBalanceTest210v() { return phaseBalanceTest210v; }
    public void setPhaseBalanceTest210v(String phaseBalanceTest210v) { this.phaseBalanceTest210v = phaseBalanceTest210v; }

    public String getInsulationResistanceMotor() { return insulationResistanceMotor; }
    public void setInsulationResistanceMotor(String insulationResistanceMotor) { this.insulationResistanceMotor = insulationResistanceMotor; }

    public String getInsulationResistanceCable() { return insulationResistanceCable; }
    public void setInsulationResistanceCable(String insulationResistanceCable) { this.insulationResistanceCable = insulationResistanceCable; }

    public String getMachineSizeHeight() { return machineSizeHeight; }
    public void setMachineSizeHeight(String machineSizeHeight) { this.machineSizeHeight = machineSizeHeight; }

    public String getMachineSizeLength() { return machineSizeLength; }
    public void setMachineSizeLength(String machineSizeLength) { this.machineSizeLength = machineSizeLength; }

    public String getMachineSizeWidth() { return machineSizeWidth; }
    public void setMachineSizeWidth(String machineSizeWidth) { this.machineSizeWidth = machineSizeWidth; }

    public Boolean getIsInOrder() {
        return isInOrder;
    }

    public void setIsInOrder(Boolean isInOrder) {
        this.isInOrder = isInOrder;
    }


    public Report getReport() { return report; }
    public void setReport(Report report) { this.report = report; }
}

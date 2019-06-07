package gui;

public class Variable {

    private String variableTrial;
    private String variableName;
    private String value;


    public Variable(String variableTrial, String variableName, String value) {
        this.variableTrial = variableTrial;
        this.variableName = variableName;
        this.value = value;
    }

    public String getVariableTrial() {
        return variableTrial;
    }

    public void setVariableTrial(String variableTrial) {
        this.variableTrial = variableTrial;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "variableTrial='" + variableTrial + '\'' +
                ", variableName='" + variableName + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}

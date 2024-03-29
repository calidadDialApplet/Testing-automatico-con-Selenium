package gui;

public class VariableNV
{
    private String variableName;
    private String value;

    public VariableNV(String variableName, String value) {
        this.variableName = variableName;
        this.value = value;
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
        return "VariableNV{" +
                "variableName='" + variableName + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}

package gui;

public class Global_Variable {

    private String variableName;
    private String value;

    public Global_Variable(String variableName, String value)
    {
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
        return "Global_Variable{" +
                "variableName='" + variableName + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}

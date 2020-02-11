package ProcedimientoRC.ParteDeShowflow;

public class Subtypology {
    private String name;
    private String result;
    private String visibleBy;

    public Subtypology(String name, String result, String visibleBy)
    {
        this.name = name;
        this.result = result;
        this.visibleBy = visibleBy;
    }

    public String getName() {
        return name;
    }

    public String getResult() {
        return result;
    }

    public String getVisibleBy() {
        return visibleBy;
    }
}

package gui;

import java.util.ArrayList;

public class H2DAO {

    public static ArrayList<String> getTypeAction(){
        ArrayList<String> typeActions = new ArrayList<>();
        typeActions.add("Click");
        typeActions.add("DragAndDrop");
        typeActions.add("Selector");
        return typeActions;
    }
    public static ArrayList<String> getSelectElementBy(){
        ArrayList<String> selectElementsBy = new ArrayList<>();
        selectElementsBy.add("Id");
        selectElementsBy.add("Xpath");
        selectElementsBy.add("CssSelector");
        selectElementsBy.add("ClassName");
        selectElementsBy.add("Name");
        return selectElementsBy;
    }
}

package gui;

import java.util.ArrayList;

public class H2DAO {

    public static ArrayList<String> getTypeAction(){
        ArrayList<String> typeActions = new ArrayList<>();
        typeActions.add("Click");
        typeActions.add("DragAndDrop");
        typeActions.add("WriteTo");
        typeActions.add("ReadFrom");
        typeActions.add("Waiting");
        return typeActions;
    }
    public static ArrayList<String> getSelectElementBy(){
        ArrayList<String> selectElementsBy = new ArrayList<>();
        selectElementsBy.add("id");
        selectElementsBy.add("xpath");
        selectElementsBy.add("cssSelector");
        selectElementsBy.add("className");
        selectElementsBy.add("name");
        return selectElementsBy;
    }
}

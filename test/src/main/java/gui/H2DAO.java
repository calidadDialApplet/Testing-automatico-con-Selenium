package gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class H2DAO {

    public static void main(String[] args){
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:./data/db","test","test");
            Statement st = conn.createStatement();
            String statement = "create table trial_actions(" +
                    "  id integer auto_increment," +
                    "  trialid integer auto_increment," +
                    "  actiontypeid text," +
                    "  selectionbyid1 text," +
                    "  value1 text," +
                    "  selectionbyid2 text," +
                    "  value2 text," +
                    "  constraint pk_trial_actions primary key(id, trialid)" +
                    ")";
            //String statement = "drop table trial_actions";
            st.execute(statement);
            System.out.println("FUNCIONA!!!!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<String> getTypeAction(){
        ArrayList<String> typeActions = new ArrayList<>();
        typeActions.add("Click");
        typeActions.add("DragAndDrop");
        typeActions.add("WriteTo");
        typeActions.add("ReadFrom");
        //typeActions.add("Waiting");
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
    public static void saveTrial(List<Action> actionList)
    {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:./data/db","test","test");
            Statement st = conn.createStatement();



            for(int i = 0; i < actionList.size(); i++)
            {
                Action currentAction = actionList.get(i);
                String statement = "insert into" +
                        " trial_actions (" +
                        " actiontypeid," +
                        " selectionbyid1," +
                        " value1," +
                        " selectionbyid2," +
                        " value2" +
                        "  )" +
                        " values" +
                        "(1, 1, 1, 1, 1)";
                        //"("+currentAction.getActionType()+", "+currentAction.getSelectElementBy()+", "+currentAction.getFirstValueArgs()+", "+currentAction.getSelectPlaceBy()+", "+currentAction.getSecondValueArgs()+")";
                st.execute(statement);
                System.out.println("Pasa2");
            }
            String statement2 = "select count(*) from trial_actions";
            System.out.println(st.execute(statement2));
            System.out.println("GUARDADO");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

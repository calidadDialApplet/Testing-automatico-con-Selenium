package gui;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class H2DAO {

    public static void main(String[] args){
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:./data/db","test","test");
            Statement st = conn.createStatement();
            String createActionTypes = "create table action_types(" +
                    "  id integer auto_increment," +
                    "  name text," +
                    "  constraint pk_trial_action primary key(id)" +
                    ")";
            String createSelectionBy = "create table selection_by(" +
                    "  id integer auto_increment," +
                    "  name text," +
                    "  constraint pk_selection_by primary key(id)" +
                    ")";
            String createTrials = "create table trials(" +
                    "  id integer auto_increment," +
                    "  name text," +
                    "  constraint pk_trials primary key(id)" +
                    ")";
            String createTrialActonsTable = "create table trial_actions(" +
                    "  id integer auto_increment," +
                    "  trialid integer," +
                    "  actiontypeid integer," +
                    "  selectionbyid1 integer," +
                    "  value1 text," +
                    "  selectionbyid2 integer," +
                    "  value2 text," +
                    "  constraint pk_trial_action_table primary key(id,trialid)" +
                    /*"  constraint fk_trials foreign key(trialid) references trials(id)" +
                    "  constraint fk_action_type foreign key(actiontypeid) references action_types(name)" +
                    "  constraint fk_selection_by foreign key(selectionbyid1) references selection_by(name)" +
                    "  constraint fk_selection_by_name foreign key(selectionbyid2) references selection_by(name)" +*/
                    ")";
            String statement = "drop table action_types";

            String alterTable = "alter table trial_actions add constraint fk_selectionbyid2 foreign key(selectionbyid2) references selection_by(id)";

            String delete = "delete from trials";
            //String poblate = "insert into trials_actions (actiontypeid, selectionbyid1, value1, selectionbyid2, value2) values (click, id, login, NULL, NULL)";
            //st.execute(statement);
            //st.execute(createActionTypes);
            //st.execute(createSelectionBy);
            //st.execute(createTrials);
            //st.execute(createTrialActonsTable);
            //st.execute(delete);

            // Todas las tablas y relaciones creadas
            //System.out.println("FUNCIONA!!!!");
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

    public static void saveTrial(List<Action> actionList, String id)
    {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:./data/db","test","test");
            Statement st = conn.createStatement();

            Integer actionTypeId;
            Integer firstValueArgs;
            String value1 = "NULL";
            Integer secondValueArgs;
            String value2 = "NULL";

            for(int i = 0; i < actionList.size(); i++)
            {
                Action currentAction = actionList.get(i);


                    actionTypeId = getIdActionType(currentAction.getActionTypeString());


                    firstValueArgs = getIdSelectElementBy(currentAction.getSelectElementByString());


                if(!currentAction.getFirstValueArgsString().equals(""))
                {
                    value1 = currentAction.getFirstValueArgsString();
                }

                    secondValueArgs = getIdSelectElementBy(currentAction.getSelectPlaceByString());

                if(!currentAction.getSecondValueArgsString().equals(""))
                {
                    value2 = currentAction.getSecondValueArgsString();
                }

                String statement = "insert into" +
                        " trial_actions ("+
                        " trialid," +
                        " actiontypeid," +
                        " selectionbyid1," +
                        " value1," +
                        " selectionbyid2," +
                        " value2" +
                        ")" +
                        " values" +
                        "('"+id+"','"+actionTypeId+"','"+firstValueArgs+"','"+value1+"','"+secondValueArgs+"','"+value2+"')";
                st.execute(statement);
                System.out.println("Pasa2");
            }
            String statement2 = "select * from trial_actions";
            st.execute(statement2);
            System.out.println(st.getResultSet());
            System.out.println("GUARDADO");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    public static void deleteTrialActions(String trialID)
    {
        try{
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:./data/db","test","test");
            Statement st = conn.createStatement();

            String deleteActions = "delete from trial_actions where trialid='"+trialID+"'";

            st.execute(deleteActions);
            System.out.println("Acciones eliminadas");


        }  catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
         }
    }

    public static void createTrial(String name)
    {
        try
        {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:./data/db","test","test");
            Statement st = conn.createStatement();


            String statement = "insert into trials (name) values('"+name+"')";
            st.execute(statement);

            String statement2 = "select * from trials";
            st.execute(statement2);
            System.out.println(st.getResultSet());

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getTrials()
    {
        ArrayList<String> trialFromTable = new ArrayList<>();
        try
        {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:./data/db","test","test");
            Statement st = conn.createStatement();

            String getTrialsStatement = "select * from trials";
            st.execute(getTrialsStatement);

            ResultSet resultSet =  st.getResultSet();
            //ResultSetMetaData metaData = resultSet.getMetaData();
            //int numberOfCols = metaData.getColumnCount();

            while (resultSet.next())
            {
               //System.out.println(resultSet.getString("id"));
               trialFromTable.add(resultSet.getString("name"));
            }
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return trialFromTable;
    }

    public static Integer getIdActionType(String actionType)
    {
        Integer id = 1; // No action type
        switch (actionType){
                case "Click":
                    id = 1;
                    break;
                case "DragAndDrop":
                    id = 2;
                    break;
                case "WriteTo":
                    id = 3;
                    break;
                case "ReadFrom":
                    id = 4;
                    break;
                default:
                    break;
            }
        return id;
    }

    public static Integer getIdSelectElementBy(String actionType)
    {
        Integer id = 1; // No action type
        switch (actionType){
            case "id":
                id = 1;
                break;
            case "xpath":
                id = 2;
                break;
            case "cssSelector":
                id = 3;
                break;
            case "className":
                id = 4;
                break;
            case "name":
                id = 5;
                break;
            default:
                break;
        }
        return id;
    }

    public static ArrayList<Action> getActions(String trialName)
    {
        ArrayList<Action> actions = new ArrayList<>();
        try
        {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:./data/db","test","test");
            Statement st = conn.createStatement();

            String id = getTrialId(trialName);

            System.out.println("ID: "+id);

            String getActionsFromTrial = "Select * from trial_actions where trialid ='"+id+"'";
            st.execute(getActionsFromTrial);
            ResultSet actionsResultSet = st.getResultSet();
            System.out.println(actionsResultSet.toString());

            while (actionsResultSet.next())
            {
                Action currentAction = new Action(actionsResultSet.getString("actiontypeid"), actionsResultSet.getString("selectionbyid1"),
                        actionsResultSet.getString("value1"), actionsResultSet.getString("selectionbyid2"), actionsResultSet.getString("value2"));
                actions.add(currentAction);
            }
             System.out.println("Llega");

        }catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return actions;
    }

    public static String getTrialId(String trialName)
    {
        String id = "NULL";
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:./data/db", "test", "test");
            Statement st = conn.createStatement();

            String getIDFromTrialName = "Select id from trials where name='" + trialName + "'";
            st.execute(getIDFromTrialName);

            ResultSet idResultSet = st.getResultSet();
            while (idResultSet.next()) {
                id = (idResultSet.getString(1));
            }
        }catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return id;
    }
}

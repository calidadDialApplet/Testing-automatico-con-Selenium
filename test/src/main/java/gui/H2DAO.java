package gui;

import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class H2DAO {

     static ArrayList<String> matchesTableName = new ArrayList<>(Arrays.asList("TRIALS","TRIAL_ACTIONS","ACTION_TYPES","SELECTION_BY"));
     static ArrayList<String> matchesColName = new ArrayList<>(Arrays.asList("ID","NAME","ID","NAME","ID","NAME","ID","TRIALID","ACTIONTYPEID","SELECTIONBYID1","VALUE1","SELECTIONBYID2","VALUE2","VALIDATION"));
     static ArrayList<String> matchesTypeName = new ArrayList<>(Arrays.asList("INTEGER","TEXT","INTEGER","TEXT","INTEGER","TEXT","INTEGER","INTEGER","INTEGER","INTEGER","TEXT","INTEGER","TEXT","INTEGER"));
     static String validationTableNameQuery = "select table_name from information_schema.tables where table_type = 'TABLE'";
     static String[] validationColNameQuerys = new String[]
    {
            "select column_name from information_schema.columns where table_name = 'TRIALS'",
            "select column_name from information_schema.columns where table_name = 'ACTION_TYPES'",
            "select column_name from information_schema.columns where table_name = 'SELECTION_BY'",
            "select column_name from information_schema.columns where table_name = 'TRIAL_ACTIONS'"
    };
    static String[] validationColTypesQuerys = new String[]
    {
            "select column_type from information_schema.columns where table_name = 'TRIALS'",
            "select column_type from information_schema.columns where table_name = 'ACTION_TYPES'",
            "select column_type from information_schema.columns where table_name = 'SELECTION_BY'",
            "select column_type from information_schema.columns where table_name = 'TRIAL_ACTIONS'"
    };

    static String[] createTables = new String[]
    {
            "create table action_types(" +
                    "  id integer auto_increment," +
                    "  name text," +
                    "  constraint pk_trial_action primary key(id)" +
                    ")",
            "create table selection_by(" +
            "  id integer auto_increment," +
                    "  name text," +
                    "  constraint pk_selection_by primary key(id)" +
                    ")",
            "create table trials(" +
                    "  id integer auto_increment," +
                    "  name text," +
                    "  constraint pk_trials primary key(id)" +
                    ")",
            "create table trial_actions(" +
                    "  id integer auto_increment," +
                    "  trialid integer," +
                    "  actiontypeid integer," +
                    "  selectionbyid1 integer," +
                    "  value1 text," +
                    "  selectionbyid2 integer," +
                    "  value2 text," +
                    "  validation integer," +
                    "  constraint pk_trial_action_table primary key(id,trialid)"+
                    ")",
            "alter table trial_actions add constraint fk_trialid foreign key(trialid) references trials(id)",
            "alter table trial_actions add constraint fk_actiontypeid foreign key(trialid) references action_types(id)",
            "alter table trial_actions add constraint fk_selectionById1 foreign key(selectionbyid1) references selection_by(id)",
            "alter table trial_actions add constraint fk_selectionById2 foreign key(selectionbyid2) references selection_by(id)",
            "insert into action_types(name) values ('Click')",
            "insert into action_types(name) values ('DragAndDrop')",
            "insert into action_types(name) values ('WriteTo')",
            "insert into action_types(name) values ('ReadFrom')",
            "insert into selection_by(name) values ('id')",
            "insert into selection_by(name) values ('xpath')",
            "insert into selection_by(name) values ('cssSelector')",
            "insert into selection_by(name) values ('className')",
            "insert into selection_by(name) values ('name')"
    };

    static String[] dropTables = new String[]{
        "drop table action_types",
        "drop table selection_by",
        "drop table trials",
        "drop table trial_actions"
    };

    static Connection connection;

    static {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:./data/db","test","test");
        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){

    }

    /*public static Connection createMainConnection()
    {

        Connection conn = null;
        try{
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:./data/db","test","test");

        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }*/

    public static ArrayList<String> getTypeAction()
    {
        ArrayList<String> typeActions = new ArrayList<>();
        try{
                Statement st = connection.createStatement();

                String getTrialsStatement = "select * from action_types";
                st.execute(getTrialsStatement);

                ResultSet resultSet =  st.getResultSet();

            while (resultSet.next())
            {
                typeActions.add(resultSet.getString("name"));
            }
                st.close();
        }catch ( SQLException e) {
            e.printStackTrace();
        }
        return typeActions;
    }

    public static ArrayList<String> getSelectElementBy(){
        ArrayList<String> selectElementsBy = new ArrayList<>();
        try{
            Statement st = connection.createStatement();

            String getTrialsStatement = "select * from selection_by";
            st.execute(getTrialsStatement);

            ResultSet resultSet =  st.getResultSet();

            while (resultSet.next())
            {
                selectElementsBy.add(resultSet.getString("name"));
            }
            st.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return selectElementsBy;
    }

    public static void saveTrial(List<Action> actionList, String id, Integer validation)
    {
        try {
            Statement st = connection.createStatement();

            for(int i = 0; i < actionList.size(); i++)
            {
                Action currentAction = actionList.get(i);


                Integer actionTypeId = getIdActionType(currentAction.getActionTypeString());
                Integer firstValueArgs = getIdSelectElementBy(currentAction.getSelectElementByString());
                String value1 = currentAction.getFirstValueArgsString();
                Integer secondValueArgs = getIdSelectElementBy(currentAction.getSelectPlaceByString());
                String value2 = currentAction.getSecondValueArgsString();


                String statement = "insert into" +
                        " trial_actions ("+
                        " trialid," +
                        " actiontypeid," +
                        " selectionbyid1," +
                        " value1," +
                        " selectionbyid2," +
                        " value2," +
                        " validation" +
                        ")" +
                        " values" +
                        "('"+id+"','"+actionTypeId+"','"+firstValueArgs+"','"+value1+"','"+secondValueArgs+"','"+value2+"','"+validation+"')";
                st.execute(statement);
                System.out.println("Pasa2");
            }
            String statement2 = "select * from trial_actions";
            st.execute(statement2);
            System.out.println(st.getResultSet());
            System.out.println("GUARDADO");
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void deleteTrialActions(String trialID)
    {
        try{
            Statement st = connection.createStatement();

            String deleteActions = "delete from trial_actions where trialid='"+trialID+"' and validation = '0'";

            st.execute(deleteActions);
            System.out.println("Acciones eliminadas");

            st.close();
        }  catch (SQLException e) {
        e.printStackTrace();
         }
    }

    public static void deleteTrialValidations(String trialID)
    {
        try{
            Statement st = connection.createStatement();

            String deleteActions = "delete from trial_actions where trialid='"+trialID+"' and validation = '1'";

            st.execute(deleteActions);
            System.out.println("Validaciones eliminadas");

            st.close();
        }  catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTrial(String trialID)
    {
        try{
            Statement st = connection.createStatement();

            String deleteTrial = "delete from trials where id='"+trialID+"'";

            st.execute(deleteTrial);
            System.out.println("Trial Eliminado");

            st.close();
        }  catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTrial(String name)
    {
        try
        {
            Statement st = connection.createStatement();

            String statement = "insert into trials (name) values('"+name+"')";
            st.execute(statement);

            String statement2 = "select * from trials";
            st.execute(statement2);
            System.out.println(st.getResultSet());

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getTrials()
    {
        ArrayList<String> trialFromTable = new ArrayList<>();
        try
        {
            Statement st = connection.createStatement();

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
            st.close();
        }
        catch (SQLException e)
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
            Statement st = connection.createStatement();

            String id = getTrialId(trialName);

            System.out.println("ID: "+id);

            String getActionsFromTrial = "Select * from trial_actions where trialid ='"+id+"' and validation = '0'";
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
            st.close();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return actions;
    }

    public static ArrayList<Action> getValidations(String trialName)
    {
        ArrayList<Action> validations = new ArrayList<>();
        try
        {
            Statement st = connection.createStatement();

            String id = getTrialId(trialName);

            System.out.println("ID: "+id);

            String getValidationsFromTrial = "Select * from trial_actions where trialid ='"+id+"' and validation = '1'";
            st.execute(getValidationsFromTrial);
            ResultSet validationsResultSet = st.getResultSet();
            System.out.println(validationsResultSet.toString());

            while (validationsResultSet.next())
            {
                Action currentAction = new Action(validationsResultSet.getString("actiontypeid"), validationsResultSet.getString("selectionbyid1"),
                        validationsResultSet.getString("value1"), validationsResultSet.getString("selectionbyid2"), validationsResultSet.getString("value2"));
                validations.add(currentAction);
            }
            System.out.println("Llega");
            st.close();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return validations;
    }

    public static String getTrialId(String trialName)
    {
        String id = "NULL";
        try {
            Statement st = connection.createStatement();

            String getIDFromTrialName = "Select id from trials where name='" + trialName + "'";
            st.execute(getIDFromTrialName);

            ResultSet idResultSet = st.getResultSet();
            while (idResultSet.next()) {
                id = (idResultSet.getString(1));
            }
            st.close();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return id;
    }

    public static boolean checkDB()
    {
        ArrayList<String> tablesName = new ArrayList<>();
        ArrayList<String> tablesColName = new ArrayList<>();
        ArrayList<String> tablesColType = new ArrayList<>();
        ResultSet resultOfQuery;

        try {
            Statement st = connection.createStatement();
            st.execute(validationTableNameQuery);
            resultOfQuery = st.getResultSet();
            while (resultOfQuery.next()){
                tablesName.add(resultOfQuery.getString(1));
            }

            if (!tablesName.equals(matchesTableName)){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Los nombres de las tablas no coinciden");
                alert.setContentText("Contacta con tu administrador :)");
                alert.showAndWait();
                return false;
            }else {
                System.out.println("Nombre de tablas coinciden");
                for (String query : validationColNameQuerys)
                {
                    st.execute(query);
                    resultOfQuery = st.getResultSet();
                    while (resultOfQuery.next())
                    {
                        tablesColName.add(resultOfQuery.getString(1));
                    }
                }
                    if (!tablesColName.equals(matchesColName))
                    {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error");
                        alert.setHeaderText("Los nombres de las columnas no coinciden");
                        alert.setContentText("Contacta con tu administrador :)");
                        alert.showAndWait();
                        return false;
                    }else{
                        System.out.println("Nombre de Columnas coinciden");
                        for (String query : validationColTypesQuerys)
                        {
                            st.execute(query);
                            resultOfQuery = st.getResultSet();
                            while (resultOfQuery.next())
                            {
                                String type = "";
                                if (resultOfQuery.getString(1).indexOf(" ") == -1)
                                {
                                     type = resultOfQuery.getString(1);
                                } else {
                                     type = resultOfQuery.getString(1).substring(0, resultOfQuery.getString(1).indexOf(" "));
                                }
                                tablesColType.add(type);
                            }
                        }
                        if (!tablesColType.equals(matchesTypeName))
                        {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Error");
                            alert.setHeaderText("Los tipos de las columnas no coinciden");
                            alert.setContentText("Contacta con tu administrador :)");
                            alert.showAndWait();
                            return false;
                        } else {
                            System.out.println("TODO VA COMO DIOS MANDA");
                        }
                    }
            }
            st.close();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public static void redoTables()
    {
        try
        {
            Statement st = connection.createStatement();
            for(String query : dropTables)
            {
                st.execute(query);
            }
            for (String query : createTables)
            {
                st.execute(query);    
            }
        
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

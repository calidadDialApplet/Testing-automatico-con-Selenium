package persistence;

import gui.Action;
import gui.Variable;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.*;

public class H2DAO {

    // TODO: Once declared, this becomes constant, so should be enums, neither ArrayList<> nor String[]
     //enum matchTableName {TRIALS,TRIAL_ACTIONS,ACTION_TYPES,SELECTION_BY} ;
     static ArrayList<String> matchesTableName = new ArrayList<>(Arrays.asList("TRIALS","TRIAL_ACTIONS","VARIABLES","ACTION_TYPES","SETTINGS","SELECTION_BY"));
     static ArrayList<String> matchesColName = new ArrayList<>(Arrays.asList("ID","NAME","ID","NAME","NAME","VALUE","SETTINGFIELD","VALUE","ID","NAME","ID","TRIALID","ACTIONTYPEID","SELECTIONBYID1","VALUE1",
                                                                            "SELECTIONBYID2","VALUE2","VALIDATION","TRIAL","VARIABLE","VALUE"));
     static ArrayList<String> matchesTypeName = new ArrayList<>(Arrays.asList("INTEGER","TEXT","INTEGER","TEXT","VARCHAR","VARCHAR","TEXT","TEXT","INTEGER","TEXT","INTEGER","INTEGER","INTEGER","INTEGER",
                                                                                "TEXT","INTEGER","TEXT","INTEGER","INTEGER","TEXT","TEXT"));
     static String validationTableNameQuery = "select table_name from information_schema.tables where table_type = 'TABLE'";

     // TODO: This could be a unique String
    /* static String[] validationColNameQuerys = new String[]
    {
            "select column_name from information_schema.columns where table_name = 'TRIALS'",
            "select column_name from information_schema.columns where table_name = 'ACTION_TYPES'",
            "select column_name from information_schema.columns where table_name = 'SELECTION_BY'",
            "select column_name from information_schema.columns where table_name = 'TRIAL_ACTIONS'"
    };*/

     static String validationColNameQuery = "select column_name from information_schema.columns where table_name = 'TRIALS' or table_name = 'ACTION_TYPES'" +
                                            "or table_name = 'SELECTION_BY' or table_name = 'TRIAL_ACTIONS' or table_name = 'SETTINGS' or table_name = 'VARIABLES'";

    // TODO: This could be a unique String
    /*static String[] validationColTypesQuerys = new String[]
    {
            "select column_type from information_schema.columns where table_name = 'TRIALS'",
            "select column_type from information_schema.columns where table_name = 'ACTION_TYPES'",
            "select column_type from information_schema.columns where table_name = 'SELECTION_BY'",
            "select column_type from information_schema.columns where table_name = 'TRIAL_ACTIONS'"
    };*/

    static String validationColTypesQuery = "select column_type from information_schema.columns where table_name = 'TRIALS' or table_name = 'ACTION_TYPES'" +
                                            "or table_name = 'SELECTION_BY' or table_name = 'TRIAL_ACTIONS' or table_name = 'SETTINGS' or table_name = 'VARIABLES'";
    // TODO: This could be a unique String
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
            "create table settings(settingField text, value text)",
            "create table variables(trial integer, variable text, value text)",
            "alter table trial_actions add constraint fk_trialid foreign key(trialid) references trials(id)",
            "alter table trial_actions add constraint fk_actiontypeid foreign key(actiontypeid) references action_types(id)",
            "alter table trial_actions add constraint fk_selectionById1 foreign key(selectionbyid1) references selection_by(id)",
            "alter table trial_actions add constraint fk_selectionById2 foreign key(selectionbyid2) references selection_by(id)",
            "alter table variables add constraint fk_trial foreign key (trial) references trials(id)",
            "insert into action_types(name) values ('Click')",
            "insert into action_types(name) values ('DragAndDrop')",
            "insert into action_types(name) values ('WriteTo')",
            "insert into action_types(name) values ('ReadFrom')",
            "insert into action_types(name) values ('SwitchTo')",
            "insert into action_types(name) values ('Waiting For')",
            "insert into action_types(name) values ('WaitTime')",
            "insert into action_types(name) values ('SwitchDefault')",
            "insert into selection_by(name) values ('id')",
            "insert into selection_by(name) values ('xpath')",
            "insert into selection_by(name) values ('cssSelector')",
            "insert into selection_by(name) values ('className')",
            "insert into selection_by(name) values ('name')",
            "insert into settings (settingField, value) values ('web', 'www.google.es')",
            "insert into settings (settingField, value) values ('browser', 'Firefox')",
            "insert into settings (settingField, value) values ('headless', 'true')",
            "insert into settings (settingField, value) values ('darktheme', 'true')"
    };

    // TODO: This could be a unique String
    /*static String[] dropTables = new String[]{
        "drop table action_types",
        "drop table selection_by",
        "drop table trials",
        "drop table trial_actions"
    };*/

    static String dropTablesQuery = "drop table action_types,selection_by,trials,trial_actions,settings,validations";

    // TODO: This should be in H2DAO constructor, and main is useless
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

        try {
            Statement st = connection.createStatement();
            String createTable = "alter table variables add constraint fk_trial foreign key (trial) references trials(id)";
            st.execute(createTable);
            /*
            ResultSet resultSet =  st.getResultSet();

            while (resultSet.next())
            {
                System.out.println(resultSet.getString("id"));
            }
            */

            System.out.println("FUNCIONA");
        } catch (SQLException e) {
            e.printStackTrace();
        }


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
        return getComboBoxElements("action_types");
    }

    public static ArrayList<String> getSelectElementBy()
    {
       return getComboBoxElements("selection_by");
    }

    public static ArrayList<String> getComboBoxElements(String comboBoxName)
    {
        ArrayList<String> comboItems = new ArrayList<>();
        try{
            Statement st = connection.createStatement();

            String Statement = "select * from "+comboBoxName+"";
            st.execute(Statement);
            ResultSet resultSet =  st.getResultSet();
            while (resultSet.next())
            {
                comboItems.add(resultSet.getString("name"));
            }
            st.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return comboItems;
    }

    // TODO: How have "id"'s become Strings instead of int ??
    public static void saveTrial(List<Action> actionList, String id, Integer validation)
    {
        try {
            Statement st = connection.createStatement();

            for(int i = 0; i < actionList.size(); i++)
            {
                Action currentAction = actionList.get(i);


                int actionTypeId = getIdActionType(currentAction.getActionTypeS());
                int firstValueArgs = getIdSelectElementBy(currentAction.getSelectElementByS());
                String value1 = currentAction.getFirstValueArgsS();
                int secondValueArgs = getIdSelectElementBy(currentAction.getSelectElementByS());
                String value2 = currentAction.getSecondValueArgsS();

                //System.out.println(""+actionTypeId+" / "+firstValueArgs+ " / "+value1+" / "+secondValueArgs+ " / "+value2);



                /*String query = "insert into" +
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
                        "('"+id+"','"+actionTypeId+"','"+firstValueArgs+"','"+value1+"','"+secondValueArgs+"','"+value2+"','"+validation+"')";*/
                String query = "insert into trial_actions (trialid, actiontypeid, selectionbyid1, value1, selectionbyid2, value2, validation)" +
                        " values(?,?,?,?,?,?,?) ";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1,id);
                preparedStatement.setInt(2,actionTypeId);
                preparedStatement.setInt(3,firstValueArgs);
                preparedStatement.setString(4,value1);
                preparedStatement.setInt(5,secondValueArgs);
                preparedStatement.setString(6,value2);
                preparedStatement.setInt(7,validation);
                preparedStatement.executeUpdate();
                //st.execute(query);
                //ResultSet rs = preparedStatement.executeQuery();
                //System.out.println("Pasa2");
            }
            String statement2 = "select * from trial_actions where id ='"+id+"'";
            st.execute(statement2);
            System.out.println(st.getResultSet());
            System.out.println("GUARDADO");
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // TODO: How have "id"'s become Strings instead of int ??
    public static void deleteTrialActions(String trialID)
    {
            executeQuery("delete from trial_actions where trialid='"+trialID+"' and validation = '0'");
            System.out.println("Acciones eliminadas");
    }

    // TODO: How have "id"'s become Strings instead of int ??
    public static void deleteTrialValidations(String trialID)
    {
            executeQuery("delete from trial_actions where trialid='"+trialID+"' and validation = '1'");
            System.out.println("Validaciones eliminadas");
    }

    // TODO: How have "id"'s become Strings instead of int ??
    public static void deleteTrial(String trialID)
    {
            executeQuery("delete from trials where id='"+trialID+"'");
            System.out.println("Trial Eliminado");
    }

    public static void createTrial(String name)
    {
        executeQuery("insert into trials (name) values('"+name+"')");
    }

    public static void executeQuery(String query)
    {
        try
        {
            Statement st = connection.createStatement();
            st.execute(query);
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

    public static int getIdActionType(String actionType)
    {
        // TODO: Although you need to use "Integer" in Maps, you can still use "int" wherever
        int result = 1;
        try{

            Statement st = connection.createStatement();
            String getIdActionType = "Select id from action_types where name = '"+actionType+"'";
            st.execute(getIdActionType);

            ResultSet resultSet = st.getResultSet();

            while (resultSet.next())
            {
                result = Integer.parseInt(resultSet.getString("id"));
            }


        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return result;

    }

    public static int getIdSelectElementBy(String selectBy)
    {
        // TODO: Although you need to use "Integer" in Maps, you can still use "int" wherever
        int result = 1;
        try{

            Statement st = connection.createStatement();
            String getSelectByIndex = "Select id from selection_by where name = '"+selectBy+"'";
            st.execute(getSelectByIndex);

            ResultSet resultSet = st.getResultSet();

            while (resultSet.next())
            {
                result = Integer.parseInt(resultSet.getString("id"));
            }


        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<Action> getActions(String trialName)
    {
        ArrayList<Action> actions = new ArrayList<>();
        try
        {
            Statement st = connection.createStatement();

            String id = getTrialID(trialName);

            System.out.println("ID: "+id);

            String getActionsFromTrial = "Select * from trial_actions where trialid ='"+id+"' and validation = '0'";
            st.execute(getActionsFromTrial);
            ResultSet actionsResultSet = st.getResultSet();
            System.out.println(actionsResultSet.toString());

            while (actionsResultSet.next())
            {

                Action currentAction = new Action(actionsResultSet.getString("actiontypeid"), actionsResultSet.getString("selectionbyid1"),
                        actionsResultSet.getString("value1"),actionsResultSet.getString("selectionbyid2"), actionsResultSet.getString("value2"));
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

            String id = getTrialID(trialName);

            System.out.println("ID: "+id);

            String getValidationsFromTrial = "Select * from trial_actions where trialid ='"+id+"' and validation = '1'";
            st.execute(getValidationsFromTrial);
            ResultSet validationsResultSet = st.getResultSet();
            System.out.println(validationsResultSet.toString());

            while (validationsResultSet.next())
            {
                Action currentValidation = new Action(validationsResultSet.getString("actiontypeid"), validationsResultSet.getString("selectionbyid1"),
                        validationsResultSet.getString("value1"), validationsResultSet.getString("selectionbyid2"), validationsResultSet.getString("value2"));
                validations.add(currentValidation);
            }
            System.out.println("Llega");
            st.close();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return validations;
    }

    public static String getTrialID(String trialName)
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

    public static void deleteTrialVariables(String trialid)
    {
        try{
            Statement st = connection.createStatement();

            String deleteVariables = "delete from variables where trial='"+trialid+"'";
            st.execute(deleteVariables);
            System.out.println("Variables Eliminadas");
            st.close();
        }catch (SQLException e){

        }
    }

    private static List<String> fillData(ResultSet set, int index) throws SQLException {
        List<String> result = new ArrayList<>();
        while (set.next()) {
            String value;
            if (set.getString(index).indexOf(" ") == -1)
                value = set.getString(index);
            else
                value = set.getString(index).substring(0, set.getString(index).indexOf(" "));

            result.add(value);
        }
        return result;
    }

    private static boolean executeQueryAndCheck(String sql, List<String> check) throws SQLException {
        Statement st = connection.createStatement();
        st.execute(sql);
        ResultSet resultOfQuery = st.getResultSet();
        List<String> tablesName = fillData(resultOfQuery, 1);
        System.out.println(tablesName);
        return tablesName.equals(check);
    }

    public static void updateTrialName(String oldName, String newName)
    {
        try{
            Statement st = connection.createStatement();
            String setName = "update trials set name = '"+newName+"' where name = '"+oldName+"'";
            st.execute(setName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getValidationsName(String trialID)
    {
        ArrayList<String> validationNames = new ArrayList<>();

        String query = "select variable from variables where trial = '?'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,trialID);
            //preparedStatement.executeUpdate();

            ResultSet resultSet =  preparedStatement.executeQuery(query);
            while (resultSet.next())
            {
                validationNames.add(resultSet.getString("variable"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return validationNames;
    }

    public static boolean checkDB()
    {
        try {

            if (!executeQueryAndCheck(validationTableNameQuery, matchesTableName)){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Los nombres de las tablas no coinciden");
                alert.setContentText("Contacta con tu administrador :)");
                alert.showAndWait();
                return false;
            }

            if (!executeQueryAndCheck(validationColNameQuery, matchesColName))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Los nombres de las columnas no coinciden");
                alert.setContentText("Contacta con tu administrador :)");
                alert.showAndWait();
                return false;
            }

            if (!executeQueryAndCheck(validationColTypesQuery, matchesTypeName))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Los tipos de las columnas no coinciden");
                alert.setContentText("Contacta con tu administrador :)");
                alert.showAndWait();
                return false;
            }
            System.out.println("TODO VA COMO DIOS MANDA");

        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public static void saveSettings(settingsObject settingsObject)
    {
            //String saveWeb = "insert into settings (settingField, value) values ('web', '"+settingsObject.getWeb()+"')";
            //String saveBrowser = "insert into settings (settingField, value) values ('browser', '"+settingsObject.getBrowser()+"')";
            //String saveHeadless = "insert into settings (settingField, value) values ('headless', '"+settingsObject.isHeadless()+"')";
            //st.execute(saveWeb);
            //st.execute(saveBrowser);
            //st.execute(saveHeadless);

        executeQuery("update settings set value = '"+settingsObject.getWeb()+"' where settingField = 'web'");
        executeQuery("update settings set value = '"+settingsObject.getBrowser()+"' where settingField = 'browser'");
        executeQuery("update settings set value = '"+settingsObject.isHeadless()+"' where settingField = 'headless'");
        executeQuery("update settings set value = '"+settingsObject.isDarktheme()+"' where settingField = 'darktheme'");

    }

    public static String getWeb()
    {
       return getSettingValue("web");
    }

    public static String getBrowser()
    {
       return getSettingValue("browser");
    }

    public static String isHeadless()
    {
        return getSettingValue("headless");
    }

    public static boolean isDarkTheme()
    {
        boolean result = false;
        if (getSettingValue("darktheme").equals("true")){
            result = true;
        }
        return result;
    }

    public static void saveTrialVariables(ArrayList<Variable> variables, String trial)
    {
        try{
            Statement st = connection.createStatement();
            for (Variable variable : variables){
                String query = "insert into variables (trial, variable, value) values ('"+variable.getVariableTrial()+"', '"+variable.getVariableName()+"', '"+variable.getValue()+"')";
                st.execute(query);
            }
            System.out.println("Variables Guardadas");
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public static ArrayList<Variable> getTrialVariables(String trial)
    {
        ArrayList<Variable> variables = new ArrayList<>();

        try{
            Statement st = connection.createStatement();
            String getVariables = "select * from variables where trial = '"+trial+"'";
            st.execute(getVariables);
            ResultSet set = st.getResultSet();
            while (set.next())
            {
              Variable variable = new Variable(set.getString(1), set.getString(2),set.getString(3));
              variables.add(variable);
            }

        }catch (SQLException e){

        }

        return variables;
    }

    // TODO: More generic, args: table and field
    public static String getSettingValue(String settingField)
    {
        String result = "";
        try {
            Statement st = connection.createStatement();
            String obtainValue = "select value from settings where settingField = '"+settingField+"'";
            st.execute(obtainValue);
            ResultSet resultSet = st.getResultSet();
            while (resultSet.next())
            {
                result = resultSet.getString("value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void redoTables()
    {
        try
        {
            Statement st = connection.createStatement();
            st.execute(dropTablesQuery);
            for (String query : createTables)
            {
                st.execute(query);    
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
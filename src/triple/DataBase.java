package triple;
import com.sun.jndi.ldap.Connection;
import com.sun.xml.internal.ws.server.ServerRtException;

import java.sql.*;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;

public class DataBase {
    Statement statement;
    String command;
    ResultSet res;
    int currentMaxId;
    DataBase(String tableName) {
        try {
            Class.forName("org.sqlite.JDBC");

            java.sql.Connection con = DriverManager.getConnection("jdbc:sqlite:"+tableName+".db");
            /*DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/?characterEncoding=UTF-8", "root", "123456");*/
            System.out.println("Connected to "+tableName+" database");
            statement = con.createStatement();


            command="CREATE TABLE IF NOT EXISTS "+tableName+"(" +
                    "record_row INT PRIMARY KEY ," +
                    "record_id INT ," +
                    "record_name TEXT," +
                    "record_time TEXT," +
                    "record_stage INT," +
                    "record_score INT," +
                    "record_others TEXT" +
                    ");";

            System.out.println(command);
            statement.executeUpdate(command);
            System.out.println("used table successful");

            currentMaxId=0;
            ArrayList<RecordData> list=QueryAll();
            for (RecordData record :
                    list) {
                currentMaxId = Math.max(record.id, currentMaxId);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void Insert(RecordData record){
        String id=Integer.toString(++currentMaxId);
        String name="\""+record.name+"\"";
        String time="\'"+RecordData.TimeToStr(record.dateTime)+"\'";
        String stage=Integer.toString( record.stage);
        String score=Integer.toString(record.score);
        String others="\""+record.situation+"\"";
        try {
            command = "INSERT INTO record" +
                    "(record_id,record_name,record_time,record_stage,record_score,record_others)" +
                    "VALUES " +
                    "("+id+","+name+","+time+","+stage+","+score+","+others+");";
            statement.executeUpdate(command);
            System.out.println(command);
            System.out.println("insert successful");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void Delete(int id){
        try {
            command = "DELETE FROM record WHERE record_id=" +Integer.toString(id)+";";
            statement.executeUpdate(command);
            System.out.println("delete #"+Integer.toString(id)+" successful");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void Update(int ID,RecordData record){
        String id=Integer.toString(++currentMaxId);
        String name="\""+record.name+"\"";
        String time="\'"+RecordData.TimeToStr(record.dateTime)+"\'";
        String stage=Integer.toString( record.stage);
        String score=Integer.toString(record.score);
        String others="\""+record.situation+"\"";
        try {
            command = "UPDATE record SET " +
                    "record_id=" +id+"," +
                    "record_name=" +name+"," +
                    "record_time="+time+"," +
                    "record_stage="+stage+"," +
                    "record_score="+score+","+
                    "record_others="+others+" "+
                    "WHERE record_id="+Integer.toString(ID)+";";
            statement.executeUpdate(command);
            System.out.println(command);
            System.out.println("update successful");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    ArrayList<RecordData> QueryAll(){
        ArrayList<RecordData> list=new ArrayList<>();
        try {
            command ="SELECT * FROM record;";
            res=statement.executeQuery(command);
            while(res.next()){
                list.add(new RecordData(res.getInt("record_id"),
                        res.getString("record_name"),
                        RecordData.StrToTime(res.getString("record_time")),
                        res.getInt("record_stage"),
                        res.getInt("record_score"),
                        res.getString("record_others")
                ));
                System.out.println(res.getInt("record_id"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        DataBase db=new DataBase("record");
        /*db.Insert(new RecordData("sherco",LocalDateTime.now(),1,100));*/
        db.QueryAll();
    }
}

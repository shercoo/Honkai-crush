package triple;
import com.sun.jndi.ldap.Connection;
import com.sun.xml.internal.ws.server.ServerRtException;

import java.sql.*;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;

public class ImageBase {
    Statement statement;
    String command;
    ResultSet res;
    ImageBase() {
        try {
            Class.forName("org.sqlite.JDBC");

            java.sql.Connection con = DriverManager.getConnection("jdbc:sqlite:images.db");
            /*DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/?characterEncoding=UTF-8", "root", "123456");*/
            System.out.println("Connected to images database");
            statement = con.createStatement();

            command="CREATE TABLE IF NOT EXISTS images(" +
                    "image_row INT PRIMARY KEY ," +
                    "image_name TEXT" +
                    ");";

            System.out.println(command);
            statement.executeUpdate(command);
            System.out.println("used table successful");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void Insert(String name){
        name="\""+name+"\"";
        try {
            command = "INSERT INTO images" +
                    "(image_name)" +
                    "VALUES " +
                    "("+name+");";
            statement.executeUpdate(command);
            System.out.println(command);
            System.out.println("insert successful");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    ArrayList<String> QueryAll(){
        ArrayList<String> list=new ArrayList<>();
        try {
            command ="SELECT * FROM images;";
            res=statement.executeQuery(command);
            while(res.next()){
                list.add(res.getString("image_name"));
                System.out.println(res.getString("image_name"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    boolean QueryExisted(String name){
        name="\""+name+"\"";
        boolean exist=false;
        try {
            command ="SELECT * FROM images WHERE image_name="+name+";";
            res=statement.executeQuery(command);
            while(res.next()){
                exist=true;
                break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return exist;
    }

    public static void main(String[] args) {
        ImageBase imageBase=new ImageBase();
        /*db.Insert(new RecordData("sherco",LocalDateTime.now(),1,100));*/
        imageBase.QueryAll();
    }
}

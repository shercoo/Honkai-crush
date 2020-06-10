package triple;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RecordData {
    int id;
    String name;
    LocalDateTime dateTime;
    int stage;
    int score;
    String situation;

    static String TimeToStr(LocalDateTime dateTime){
        return (dateTime.toString().replace("T"," ").split("\\."))[0];
    }
    static LocalDateTime StrToTime(String str){
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(str,formatter);
    }

    RecordData(){
        id=0;
    }
    RecordData(String name,LocalDateTime dateTime,int stage,int score,String situation){
        this.name=name;
        this.dateTime=dateTime;
        this.stage=stage;
        this.score=score;
        this.situation=situation;
    }
    RecordData(int id,String name,LocalDateTime dateTime,int stage,int score,String situation){
        this(name,dateTime,stage,score,situation);
        this.id=id;
    }


}

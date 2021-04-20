import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.data.DataObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class SaveData {
    public static void saveData() throws IOException {
        ArrayList<String>allUserIds = new ArrayList<>();
        for(User user:Main.allUsers){
            allUserIds.add(user.getId());
        }
        ArrayList<Integer>totalgamesplayed = new ArrayList<>();
        ArrayList<Integer>rpsgamesplayed= new ArrayList<>();
        ArrayList<Integer>rpswinstreak= new ArrayList<>();
        ArrayList<Integer>totalrpswins= new ArrayList<>();
        ArrayList<Integer>totalrpslosses= new ArrayList<>();
        ArrayList<ArrayList<Integer>>rpswins = new ArrayList<>();
        for(Player player:Main.allPlayers){
            totalgamesplayed.add(player.totalgamesplayed);
            rpsgamesplayed.add(player.rpsgamesplayed);
            rpswinstreak.add(player.rpswinstreak);
            totalrpswins.add(player.totalrpswins);
            totalrpslosses.add(player.totalrpslosses);
            rpswins.add(player.rpswins);
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH;mm");
        LocalDateTime now = LocalDateTime.now();
        DataObject dataObject = DataObject.empty();
        dataObject.put("prefix",Main.prefix);
        dataObject.put("ids",allUserIds);
        dataObject.put("totalgamesplayed",totalgamesplayed);
        dataObject.put("rpsgamesplayed",rpsgamesplayed);
        dataObject.put("rpswinstreak",rpswinstreak);
        dataObject.put("totalrpswins",totalrpswins);
        dataObject.put("totalrpslosses",totalrpslosses);
        dataObject.put("rpswins",rpswins);
        File saveFile = new File("Saves/"+dtf.format(now)+".json");
        File recentSave = new File("BoardBotData.json");
        FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
        fileOutputStream.write(dataObject.toJson());
        fileOutputStream = new FileOutputStream(recentSave);
        fileOutputStream.write(dataObject.toJson());
        fileOutputStream.close();
        System.out.println("Save Successful.");
    }
    public static void loadData() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("BoardBotData.json");
        DataObject dataObject = DataObject.fromJson(fileInputStream);
        Main.prefix = (String)dataObject.get("prefix");
        String userID;
        int totalgamesplayed;
        int rpsgamesplayed;
        int rpswinstreak;
        int totalrpswins;
        int totalrpslosses;
        for(Object id : dataObject.getArray("ids")){
            Main.allUsers.add(User.fromId(""+id));
            System.out.println(""+id);
        }
        for(int i=0;i<dataObject.getArray("ids").length();i++){
            ArrayList<Integer>temp=new ArrayList<>();
            for(Object num:dataObject.getArray("rpswins").getArray(i)){
                temp.add(Integer.parseInt(""+num));
            }
            Main.allPlayers.add(new Player(dataObject.getArray("ids").getString(i),
                    dataObject.getArray("totalgamesplayed").getInt(i),
                    dataObject.getArray("rpsgamesplayed").getInt(i),
                    dataObject.getArray("rpswinstreak").getInt(i),
                    dataObject.getArray("totalrpswins").getInt(i),
                    dataObject.getArray("totalrpslosses").getInt(i),
                    temp
            ));
        }
        System.out.println("Load Successful");
    }
}
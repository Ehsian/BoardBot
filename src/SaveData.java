import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SaveData {
    public static void saveData(Player user) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("ignore/data/"+user.userID+".txt"));
        out.writeObject(user);
        System.out.println("Data Successfully Saved.");
    }
    public static void saveData() throws IOException {
        for(Player user:Main.allPlayers){
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("ignore/data/"+user.userID+".txt"));
            out.writeObject(user);
        }
        System.out.println("Data Successfully Saved.");
    }
    public static void loadData() throws IOException,ClassNotFoundException {
        File folder = new File("ignore/data/");
        File[] allFiles = folder.listFiles();

        assert allFiles != null;
        for (File file : allFiles) {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("ignore/data/"+file.getName()));
            Player temp = (Player)in.readObject();
            Main.allUsers.add(User.fromId(temp.userID));
            Main.allPlayers.add(temp);
        }
        System.out.println("Data Successfully Loaded.");
    }
}
import net.dv8tion.jda.api.entities.User;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    public String userID;
    public int totalgamesplayed;
    //-------------------------------------------RockPaperScissors
    public int rpsgamesplayed;
    public int rpswinstreak;
    public int totalrpswins;
    public int totalrpslosses;
    public ArrayList<Integer> rpswins = new ArrayList<>(); //rps wins against user. rpswins index = allUsers Index

    //-------------------------------------------Battleship
    public int batshipgamesplayed;
    public int batshipwinstreak;
    public int totalbatshipwins;
    public int totalbatshiplosses;
    public ArrayList<Integer> batshipwins = new ArrayList<>();

    //-------------------------------------------
    public Player(String userID){
        this.userID = userID;
        rpsgamesplayed = 0;
        rpswinstreak = 0;
        batshipgamesplayed = 0;
        batshipwinstreak = 0;
    }
    public Player(String userID,int totalgamesplayed,int rpsgamesplayed,int rpswinstreak,int totalrpswins,int totalrpslosses,ArrayList<Integer> rpswins){
        this.userID = userID;
        this.totalgamesplayed = totalgamesplayed;
        this.rpsgamesplayed = rpsgamesplayed;
        this.rpswinstreak = rpswinstreak;
        this.totalrpswins = totalrpswins;
        this.totalrpslosses = totalrpslosses;
        this.rpswins = rpswins;
    }
    public void Save() throws IOException{
        SaveData.saveData(Tools.getPlayer(User.fromId(userID)));
    }

    //-------------------------------------------RockPaperScissors
    public void winRps(User opponent){
        totalrpswins++;
        rpswinstreak++;
        rpsgamesplayed++;
        try{
            rpswins.set(Main.allUsers.indexOf(opponent),rpswins.get(Main.allUsers.indexOf(opponent))+1);
        }catch(Exception e){
            while(rpswins.size()-1<Main.allUsers.indexOf(opponent)){
                rpswins.add(0);
            }
            rpswins.set(Main.allUsers.indexOf(opponent),rpswins.get(Main.allUsers.indexOf(opponent))+1);
        }
        totalgamesplayed++;
        try{
            Save();
        }
        catch (Exception ignored){}
    }
    public void loseRps(){
        totalrpslosses++;
        rpswinstreak=0;
        rpsgamesplayed++;
        totalgamesplayed++;
        try{
            Save();
        }
        catch (Exception ignored){}
    }
    public int getRps1v1Stats(User opponent){
        try{
            return rpswins.get(Main.allUsers.indexOf(opponent));
        }catch(Exception e){
            while(rpswins.size()-1<Main.allUsers.indexOf(opponent)){
                rpswins.add(0);
            }
            return rpswins.get(Main.allUsers.indexOf(opponent));
        }
    }

    //-------------------------------------------Battleship
    public void winBatShip(User opponent){
        totalbatshipwins++;
        batshipwinstreak++;
        batshipgamesplayed++;
        try{
            batshipwins.set(Main.allUsers.indexOf(opponent),batshipwins.get(Main.allUsers.indexOf(opponent))+1);
        }catch(Exception e){
            while(batshipwins.size()-1<Main.allUsers.indexOf(opponent)){
                batshipwins.add(0);
            }
            batshipwins.set(Main.allUsers.indexOf(opponent),batshipwins.get(Main.allUsers.indexOf(opponent))+1);
        }
        totalgamesplayed++;
        try{
            Save();
        }
        catch (Exception ignored){}
    }
    public void loseBatShip(){
        totalbatshiplosses++;
        batshipwinstreak=0;
        batshipgamesplayed++;
        totalgamesplayed++;
        try{
            Save();
        }
        catch (Exception ignored){}
    }
    public int getBatShip1v1Stats(User opponent){
        try{
            return batshipwins.get(Main.allUsers.indexOf(opponent));
        }catch(Exception e){
            while(batshipwins.size()-1<Main.allUsers.indexOf(opponent)){
                batshipwins.add(0);
            }
            return batshipwins.get(Main.allUsers.indexOf(opponent));
        }
    }
}

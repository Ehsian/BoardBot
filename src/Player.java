import net.dv8tion.jda.api.entities.User;

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
    public Player(String userID){
        this.userID = userID;
        rpsgamesplayed = 0;
        rpswinstreak = 0;
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
    }
    public void loseRps(){
        totalrpslosses++;
        rpswinstreak=0;
        rpsgamesplayed++;
        totalgamesplayed++;
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
}

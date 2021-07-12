import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Leaderboard extends ListenerAdapter{
    static String events = "`total` - Leaderboard for total games played\n" +
            "`wins` - Leaderboard for total games won\n" +
            "`losses` - Leaderboard for total games lost\n" +
            "`rpstotal` - Leaderboard for total Rock Paper Scissors games played\n" +
            "`rpswins` - Leaderboard for Rock Paper Scissors games won\n" +
            "`rpswinstreak` - Leaderboard for highest current Rock Paper Scissors winstreak\n" +
            "`batshiptotal` - Leaderboard for total Battleship games played\n" +
            "`batshipwins` - Leaderboard for Battleship games won\n" +
            "`batshipwinstreak` - Leaderboard for highest current Battleship winstreak";


    String[]args;
    GuildMessageReceivedEvent event;
    public Leaderboard(String[] args, GuildMessageReceivedEvent event){
        this.args = args;
        this.event = event;
        main();
    }
    public void main() {
        EmbedBuilder embed = new EmbedBuilder();
        ArrayList<Player>temp = new ArrayList<>(Main.allPlayers);
        if(args.length>1){
            switch(args[1]){
                case "total":
                    for(int i=0;i<10;i++){
                        int indexofmax = 0;
                        int total=Main.allPlayers.get(indexofmax).totalgamesplayed;
                        for(int j=1;j<temp.size();j++){
                            if(User.fromId(temp.get(j).userID).equals(Main.jda.getSelfUser())){
                                continue;
                            }
                            if(temp.get(j).totalgamesplayed>total){
                                indexofmax = j;
                                total = temp.get(j).totalgamesplayed;
                            }
                        }
                        Player max = temp.get(indexofmax);
                        User user = Main.jda.retrieveUserById(max.userID).complete();
                        embed.addField(i +1+") "+user.getName(),""+max.totalgamesplayed,false);
                        temp.remove(max);
                        if(temp.size()==1){
                            break;
                        }
                    }
                    event.getChannel().sendMessage(embed.build()).queue();
                    return;

                case "wins":
                    for(int i=0;i<10;i++){
                        int indexofmax = 0;
                        int total=Main.allPlayers.get(indexofmax).totalrpswins+Main.allPlayers.get(indexofmax).totalbatshipwins;
                        for(int j=1;j<temp.size();j++){
                            if(User.fromId(temp.get(j).userID).equals(Main.jda.getSelfUser())){
                                continue;
                            }
                            if(temp.get(j).totalrpswins+temp.get(j).totalbatshipwins>total){
                                indexofmax = j;
                                total = temp.get(j).totalrpswins+temp.get(j).totalbatshipwins;
                            }
                        }
                        Player max = temp.get(indexofmax);
                        User user = Main.jda.retrieveUserById(max.userID).complete();
                        embed.addField(i +1+") "+user.getName(),max.totalrpswins+max.totalbatshipwins+"",false);
                        temp.remove(max);
                        if(temp.size()==1){
                            break;
                        }
                    }
                    event.getChannel().sendMessage(embed.build()).queue();
                    return;

                case "losses":
                    for(int i=0;i<10;i++){
                        int indexofmax = 0;
                        int total=Main.allPlayers.get(indexofmax).totalrpslosses+Main.allPlayers.get(indexofmax).totalbatshiplosses;
                        for(int j=1;j<temp.size();j++){
                            if(User.fromId(temp.get(j).userID).equals(Main.jda.getSelfUser())){
                                continue;
                            }
                            if(temp.get(j).totalrpslosses+temp.get(j).totalbatshiplosses>total){
                                indexofmax = j;
                                total = temp.get(j).totalrpslosses+temp.get(j).totalbatshiplosses;
                            }
                        }
                        Player max = temp.get(indexofmax);
                        User user = Main.jda.retrieveUserById(max.userID).complete();
                        embed.addField(i +1+") "+user.getName(),max.totalrpslosses+max.totalbatshiplosses+"",false);
                        temp.remove(max);
                        if(temp.size()==1){
                            break;
                        }
                    }
                    event.getChannel().sendMessage(embed.build()).queue();
                    return;

                case "rpstotal":
                    for(int i=0;i<10;i++){
                        int indexofmax = 0;
                        int total=Main.allPlayers.get(indexofmax).rpsgamesplayed;
                        for(int j=1;j<temp.size();j++){
                            if(User.fromId(temp.get(j).userID).equals(Main.jda.getSelfUser())){
                                continue;
                            }
                            if(temp.get(j).rpsgamesplayed>total){
                                indexofmax = j;
                                total = temp.get(j).rpsgamesplayed;
                            }
                        }
                        Player max = temp.get(indexofmax);
                        User user = Main.jda.retrieveUserById(max.userID).complete();
                        embed.addField(i +1+") "+user.getName(),""+max.rpsgamesplayed,false);
                        temp.remove(max);
                        if(temp.size()==1){
                            break;
                        }
                    }
                    event.getChannel().sendMessage(embed.build()).queue();
                    return;

                case "rpswins":
                    for(int i=0;i<10;i++) {
                        int indexofmax = 0;
                        int total = Main.allPlayers.get(indexofmax).totalrpswins;
                        for (int j = 1; j < temp.size(); j++) {
                            if (User.fromId(temp.get(j).userID).equals(Main.jda.getSelfUser())) {
                                continue;
                            }
                            if (temp.get(j).totalrpswins > total) {
                                indexofmax = j;
                                total = temp.get(j).totalrpswins;
                            }
                        }
                        Player max = temp.get(indexofmax);
                        User user = Main.jda.retrieveUserById(max.userID).complete();
                        embed.addField(i + 1 + ") " + user.getName(), "" + max.totalrpswins, false);
                        temp.remove(max);
                        if (temp.size() == 1) {
                            break;
                        }
                    }
                    event.getChannel().sendMessage(embed.build()).queue();
                    return;

                case "rpswinstreak":
                    for(int i=0;i<10;i++) {
                        int indexofmax = 0;
                        int total = Main.allPlayers.get(indexofmax).rpswinstreak;
                        for (int j = 1; j < temp.size(); j++) {
                            if (User.fromId(temp.get(j).userID).equals(Main.jda.getSelfUser())) {
                                continue;
                            }
                            if (temp.get(j).rpswinstreak > total) {
                                indexofmax = j;
                                total = temp.get(j).rpswinstreak;
                            }
                        }
                        Player max = temp.get(indexofmax);
                        User user = Main.jda.retrieveUserById(max.userID).complete();
                        embed.addField(i + 1 + ") " + user.getName(), "" + max.rpswinstreak, false);
                        temp.remove(max);
                        if (temp.size() == 1) {
                            break;
                        }
                    }
                    event.getChannel().sendMessage(embed.build()).queue();
                    return;

                case "batshiptotal":
                    for(int i=0;i<10;i++){
                        int indexofmax = 0;
                        int total=Main.allPlayers.get(indexofmax).batshipgamesplayed;
                        for(int j=1;j<temp.size();j++){
                            if(User.fromId(temp.get(j).userID).equals(Main.jda.getSelfUser())){
                                continue;
                            }
                            if(temp.get(j).batshipgamesplayed>total){
                                indexofmax = j;
                                total = temp.get(j).batshipgamesplayed;
                            }
                        }
                        Player max = temp.get(indexofmax);
                        User user = Main.jda.retrieveUserById(max.userID).complete();
                        embed.addField(i +1+") "+user.getName(),""+max.batshipgamesplayed,false);
                        temp.remove(max);
                        if(temp.size()==1){
                            break;
                        }
                    }
                    event.getChannel().sendMessage(embed.build()).queue();
                    return;

                case "battleshipwins":
                    for(int i=0;i<10;i++) {
                        int indexofmax = 0;
                        int total = Main.allPlayers.get(indexofmax).totalbatshipwins;
                        for (int j = 1; j < temp.size(); j++) {
                            if (User.fromId(temp.get(j).userID).equals(Main.jda.getSelfUser())) {
                                continue;
                            }
                            if (temp.get(j).totalbatshipwins > total) {
                                indexofmax = j;
                                total = temp.get(j).totalbatshipwins;
                            }
                        }
                        Player max = temp.get(indexofmax);
                        User user = Main.jda.retrieveUserById(max.userID).complete();
                        embed.addField(i + 1 + ") " + user.getName(), "" + max.totalbatshipwins, false);
                        temp.remove(max);
                        if (temp.size() == 1) {
                            break;
                        }
                    }
                    event.getChannel().sendMessage(embed.build()).queue();
                    return;

                case "batshipwinstreak":
                    for(int i=0;i<10;i++) {
                        int indexofmax = 0;
                        int total = Main.allPlayers.get(indexofmax).batshipwinstreak;
                        for (int j = 1; j < temp.size(); j++) {
                            if (User.fromId(temp.get(j).userID).equals(Main.jda.getSelfUser())) {
                                continue;
                            }
                            if (temp.get(j).batshipwinstreak > total) {
                                indexofmax = j;
                                total = temp.get(j).batshipwinstreak;
                            }
                        }
                        Player max = temp.get(indexofmax);
                        User user = Main.jda.retrieveUserById(max.userID).complete();
                        embed.addField(i + 1 + ") " + user.getName(), "" + max.batshipwinstreak, false);
                        temp.remove(max);
                        if (temp.size() == 1) {
                            break;
                        }
                    }
                    event.getChannel().sendMessage(embed.build()).queue();
                    return;

                default:
                    event.getChannel().sendMessage("There is no such category!\nType ~leaderboard for a list of all available categories.").queue();
            }
        }
        else{
            embed.setTitle("Leaderboards");
            embed.setDescription("Page 1 - Click the arrows for more commands.");
            embed.addField("Available Leaderboards:",events,false);
            embed.setFooter("Bot created by Ehsian#7062", Objects.requireNonNull(event.getMember()).getUser().getAvatarUrl());
            embed.setColor(0x00e5ff);
            event.getChannel().sendMessage(embed.build()).queue();
            embed.clear();
        }
    }
}

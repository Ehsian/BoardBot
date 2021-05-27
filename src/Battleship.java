import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/*
TODO:
    - Timeout in game (prevent hostage holding) DONE
    - ff / resign (when need to leave or you know you will lose already) DONE
    - "You sunk my battleship!"
    - List which ships have not been placed yet (improve setup)
    - Ranking/Rating
        - Leaderboard
    - Aborting game before game start (during setup) DONE
    - Rules?
 */



public class Battleship extends ListenerAdapter {

    boolean reactable;

    GuildMessageReceivedEvent event;
    String[]args;
    User player1;
    User player2;
    User turn;
    boolean gameActive; //false == setup, true == battle

    int[][]map1 = new int[10][10]; //0 is water, 1 is ship, 2 is miss, 3 is hit
    int[][]map2 = new int[10][10];
    int[]ships1 = {1,2,1,1};
    int[]ships2 = {1,2,1,1};

    Timer p1timer = new Timer();
    Timer p2timer = new Timer();
    Timer timer;

    TimerTask terminate1 = new TimerTask(){
        @Override
        public void run(){
            if(reactable){
                Main.inGame.remove(player1);
                reactable = false;
                event.getChannel().sendMessage("Operation terminated. (Timeout)").queue();
            }
        }
    };

    TimerTask p1timeout = new TimerTask() {
        @Override
        public void run() {
            if(gameActive){
                player1.openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("You spent too long before your next move. (Timeout)").queue();
                    privateChannel.sendMessage("You have been given a loss for your inactivity.").queue();
                });
                Main.inGame.remove(player1);
                Main.inGame.remove(player2);
                Tools.getPlayer(player2).winBatShip(player1);
                Tools.getPlayer(player1).loseBatShip();
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Results");
                embed.setColor(0x00e5ff);
                embed.setDescription("**"+player1.getName()+" loses to "+player2.getName()+"**");
                embed.addField("**"+player1.getName()+"** vs **"+player2.getName()+"**",Tools.getPlayer(player1).getBatShip1v1Stats(player2)+" - "+Tools.getPlayer(player2).getBatShip1v1Stats(player1),false);
                player1 = null;
                player2 = null;
                event.getChannel().sendMessage(embed.build()).queue();
                embed.clear();
                player2.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You win! (Your opponent has timed out.)").queue());
            }
        }
    };
    TimerTask p2timeout = new TimerTask() {
        @Override
        public void run() {
            if(gameActive){
                player2.openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("You spent too long before your next move. (Timeout)").queue();
                    privateChannel.sendMessage("You have been given a loss for your inactivity.").queue();
                });
                Main.inGame.remove(player1);
                Main.inGame.remove(player2);
                Tools.getPlayer(player1).winBatShip(player2);
                Tools.getPlayer(player2).loseBatShip();
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Results");
                embed.setColor(0x00e5ff);
                embed.setDescription("**"+player1.getName()+" wins against "+player2.getName()+"**");
                embed.addField("**"+player1.getName()+"** vs **"+player2.getName()+"**",Tools.getPlayer(player1).getBatShip1v1Stats(player2)+" - "+Tools.getPlayer(player2).getBatShip1v1Stats(player1),false);
                player1 = null;
                player2 = null;
                event.getChannel().sendMessage(embed.build()).queue();
                embed.clear();
                player1.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You win! (Your opponent has timed out.)").queue());
            }
        }
    };
    TimerTask abort = new TimerTask(){
        @Override
        public void run(){
            if(!gameActive){
                player1.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Game aborted.").queue());
                player2.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Game aborted.").queue());
                Main.inGame.remove(player1);
                Main.inGame.remove(player2);
                player1 = null;
                player2 = null;
                event.getChannel().sendMessage("Game aborted.").queue();
            }
        }
    };

    public Battleship(String[]args, GuildMessageReceivedEvent event){
        this.event = event;
        this.args = args;
        main();
    }
    public void main(){
        player1 = event.getAuthor();
        if(Main.inGame.contains(player1)){
            event.getChannel().sendMessage("Challenge failed. (Finish your current game first!)").queue();
            player1 = null;
            return;
        } else{
            Main.inGame.add(player1);
        }
        try{
            player2 = event.getMessage().getMentionedUsers().get(0);
            if(player2.isBot()||player2.equals(player1)){
                event.getChannel().sendMessage("Please challenge a valid user.").queue();
                Main.inGame.remove(player1);
                player1 = null;
            } else{
                event.getChannel().sendMessage(String.format("<@%s>, you have 10 seconds to respond to this challenge.",player2.getId())).queue(message -> {
                    message.addReaction("✅").queue();
                    message.addReaction("❎").queue();
                    reactable = true;
                });
                Timer timer = new Timer();
                timer.schedule(terminate1,10000);
            }
        }
        catch(Exception e){
            event.getChannel().sendMessage("Please challenge a valid user.").queue();
            Main.inGame.remove(player1);
        }
    }
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event){
        if(event.getReactionEmote().getName().equals("✅")&&event.getMember().getUser().equals(player2)&&reactable){
            if(Main.inGame.contains(player2)){
                event.getChannel().sendMessage("Challenge failed. (Opponent is already in a game.)").queue();
                player2 = null;
                return;
            } else{
                Main.inGame.add(player2);
            }
            reactable = false;
            turn = Tools.firstTurn(player1,player2);
            event.getChannel().sendMessage("Commencing game... (Check your DMs)").queue();
            player1.openPrivateChannel().queue(privateChannel -> {
                printMap(player1,true,privateChannel);
                privateChannel.sendMessage("Deploy your ships!").queue();
                privateChannel.sendMessage("Format example: `f3-f5` or `d3-g3`").queue();
            });
            player2.openPrivateChannel().queue(privateChannel -> {
                printMap(player2,true,privateChannel);
                privateChannel.sendMessage("Deploy your ships!").queue();
                privateChannel.sendMessage("Format example: `f3-f5` or `d3-g3`").queue();
            });
            timer = new Timer();
            timer.schedule(abort,300000);
        }
        else if(event.getReactionEmote().getName().equals("❎")&&event.getMember().getUser().equals(player2)&&reactable){
            reactable = false;
            event.getChannel().sendMessage("Challenge declined.").queue();
            Main.inGame.remove(player1);
        }
    }
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event){
        if(event.getAuthor().isBot()||!event.getAuthor().equals(player1)&&!event.getAuthor().equals(player2)){
            return;
        }
        String message = event.getMessage().getContentRaw().toLowerCase();
        if(!gameActive){
            if(message.equals("/ff")){
                player1.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Game aborted.").queue());
                player2.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Game aborted.").queue());
                Main.inGame.remove(player1);
                Main.inGame.remove(player2);
                player1 = null;
                player2 = null;
                this.event.getChannel().sendMessage("Game aborted.").queue();
                return;
            }

            int row1,row2,col1,col2;
            if(!message.contains("-")){
                event.getChannel().sendMessage("Please send the coordinates separated by dash.").queue();
                event.getChannel().sendMessage("E.g. `a1-a3`").queue();
                return;
            }
            String[]coords = message.split("-");
            if(coords.length>2){
                event.getChannel().sendMessage("Please send a valid set of coordinates.").queue();
                event.getChannel().sendMessage("E.g. `a1-a3`").queue();
                return;
            }
            if(Tools.toInteger(coords[1].charAt(0))==-1||Tools.toInteger(coords[0].charAt(0))==-1){
                event.getChannel().sendMessage("Please enter valid rows. (a-j)").queue();
                return;
            }
            row1 = Tools.toInteger(coords[0].charAt(0));
            row2 = Tools.toInteger(coords[1].charAt(0));
            try{
                if(Integer.parseInt(coords[1].substring(1))>10||Integer.parseInt(coords[0].substring(1))>10){
                    event.getChannel().sendMessage("Please enter valid columns. (1-10)").queue();
                    return;
                }
            } catch(Exception e){
                event.getChannel().sendMessage("Please send coordinates in the valid format. (row letter followed by column number)").queue();
                event.getChannel().sendMessage("E.g. `f8`").queue();
                return;
            }
            col1 = Integer.parseInt(coords[0].substring(1));
            col2 = Integer.parseInt(coords[1].substring(1));
            if(!(col1==col2||row1==row2)){
                event.getChannel().sendMessage("Invalid coordinates! Please send coordinates with either the same row or the same column.").queue();
                return;
            }
            if(col1==col2){
                int shipsize = Math.abs(row1-row2);
                if(shipsize>4||shipsize<1){
                    event.getChannel().sendMessage("You cannot deploy a ship of this size.").queue();
                    return;
                }
                for(int i=Math.min(row1,row2)-1;i<Math.max(row1,row2);i++){
                    if(event.getAuthor().equals(player1)&&map1[i][col1-1]==1||event.getAuthor().equals(player2)&&map2[i][col1-1]==1){
                        event.getChannel().sendMessage("Ship locations cannot overlap.").queue();
                        return;
                    }
                }
                if(event.getAuthor().equals(player1)){
                    if(ships1[shipsize-1]==0){
                        event.getChannel().sendMessage("You have already deployed all possible ships of this size.").queue();
                        return;
                    }
                    ships1[shipsize-1]--;
                }
                else if(event.getAuthor().equals(player2)){
                    if(ships2[shipsize-1]==0){
                        event.getChannel().sendMessage("You have already deployed all possible ships of this size.").queue();
                        return;
                    }
                    ships2[shipsize-1]--;
                }
                for(int i=Math.min(row1,row2)-1;i<Math.max(row1,row2);i++){
                    if(event.getAuthor().equals(player1)){
                        map1[i][col1-1]=1;
                    }
                    else{
                        map2[i][col2-1]=1;
                    }
                }
            }
            else{
                int shipsize = Math.abs(col1-col2);
                if(shipsize>4||shipsize<1){
                    event.getChannel().sendMessage("You cannot create a ship of this size.").queue();
                    return;
                }
                for(int i=Math.min(col1,col2)-1;i<Math.max(col1,col2);i++){
                    if(event.getAuthor().equals(player1)&&map1[row1-1][i]==1||event.getAuthor().equals(player2)&&map2[row1-1][i]==1){
                        event.getChannel().sendMessage("Ship locations cannot overlap.").queue();
                        return;
                    }
                }
                if(event.getAuthor().equals(player1)){
                    if(ships1[shipsize-1]==0){
                        event.getChannel().sendMessage("You have already deployed all possible ships of this size.").queue();
                        return;
                    }
                    ships1[shipsize-1]--;
                }
                else if(event.getAuthor().equals(player2)){
                    if(ships2[shipsize-1]==0){
                        event.getChannel().sendMessage("You have already deployed all possible ships of this size.").queue();
                        return;
                    }
                    ships2[shipsize-1]--;
                }
                for(int i=Math.min(col1,col2)-1;i<Math.max(col1,col2);i++){
                    if(event.getAuthor().equals(player1)){
                        map1[row1-1][i]=1;
                    }
                    else{
                        map2[row2-1][i]=1;
                    }
                }
            }
            printMap(event.getAuthor(),true,event.getChannel());
            if(!((Arrays.toString(ships1)+Arrays.toString(ships2)).contains("1")||(Arrays.toString(ships1)+Arrays.toString(ships2)).contains("2"))){
                gameActive = true;
                timer.cancel();
                turn.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Your turn!").queue());
                if(turn.equals(player1)){
                    p1timer.schedule(p1timeout,120000);
                }
                else if(turn.equals(player2)){
                    p2timer.schedule(p2timeout,120000);
                }
            }
        }
        else { //gameActive = true
            if(message.equals("/ff")){
                p1timer.cancel();
                p2timer.cancel();
                if(event.getAuthor().equals(player1)){
                    printMap(player1,true,player2.openPrivateChannel().complete());
                    printMap(player2,true,event.getChannel());
                    event.getChannel().sendMessage("You lost! (Resign)").queue();
                    player2.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You won! (Opponent Resigned)").queue());
                    Main.inGame.remove(player1);
                    Main.inGame.remove(player2);
                    Tools.getPlayer(player2).winBatShip(player1);
                    Tools.getPlayer(player1).loseBatShip();
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Results");
                    embed.setColor(0x00e5ff);
                    embed.setDescription("**"+player1.getName()+" loses to "+player2.getName()+"**");
                    embed.addField("**"+player1.getName()+"** vs **"+player2.getName()+"**",Tools.getPlayer(player1).getBatShip1v1Stats(player2)+" - "+Tools.getPlayer(player2).getBatShip1v1Stats(player1),false);
                    player1 = null;
                    player2 = null;
                    this.event.getChannel().sendMessage(embed.build()).queue();
                    embed.clear();
                }else{ //player2
                    printMap(player2,true,player1.openPrivateChannel().complete());
                    printMap(player1,true,event.getChannel());
                    event.getChannel().sendMessage("You lost!").queue();
                    player1.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You won! (Opponent Resigned)").queue());
                    Main.inGame.remove(player1);
                    Main.inGame.remove(player2);
                    Tools.getPlayer(player1).winBatShip(player2);
                    Tools.getPlayer(player2).loseBatShip();
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Results");
                    embed.setColor(0x00e5ff);
                    embed.setDescription("**"+player1.getName()+" wins against "+player2.getName()+"**");
                    embed.addField("**"+player1.getName()+"** vs **"+player2.getName()+"**",Tools.getPlayer(player1).getBatShip1v1Stats(player2)+" - "+Tools.getPlayer(player2).getBatShip1v1Stats(player1),false);
                    player1 = null;
                    player2 = null;
                    this.event.getChannel().sendMessage(embed.build()).queue();
                    embed.clear();
                }
                return;
            }
            if(!event.getAuthor().equals(turn)){
                return;
            }
            int row;
            int column;
            //to be implemented (battle)
            if(Tools.toInteger(message.charAt(0))==-1){
                event.getChannel().sendMessage("Please enter a valid row. (a-j)").queue();
                return;
            }
            row = Tools.toInteger(message.charAt(0));
            try{
                if(Integer.parseInt(message.substring(1))>10){
                    event.getChannel().sendMessage("Please enter a valid column. (1-10)").queue();
                    return;
                }
            } catch(Exception e){
                event.getChannel().sendMessage("Please send coordinates in a valid format. (row letter followed by column number)").queue();
                event.getChannel().sendMessage("E.g. `f8`").queue();
                return;
            }
            column = Integer.parseInt(message.substring(1));
            if(turn.equals(player1)){
                switch(map2[row-1][column-1]){
                    case 0 -> {
                        map2[row-1][column-1] = 2;
                        event.getChannel().sendMessage("Miss!").queue();
                    }
                    case 1 -> {
                        map2[row-1][column-1] = 3;
                        event.getChannel().sendMessage("Hit!").queue();
                        boolean allSunk = true;
                        for(int[]arr:map2){
                            if(Arrays.toString(arr).contains("1")){
                                allSunk = false;
                                break;
                            }
                        }
                        if(allSunk){
                            printMap(player1,true,player2.openPrivateChannel().complete());
                            printMap(player2,true,event.getChannel());
                            event.getChannel().sendMessage("You win!").queue();
                            player2.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You lost!").queue());
                            Main.inGame.remove(player1);
                            Main.inGame.remove(player2);
                            Tools.getPlayer(player1).winBatShip(player2);
                            Tools.getPlayer(player2).loseBatShip();
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle("Results");
                            embed.setColor(0x00e5ff);
                            embed.setDescription("**"+player1.getName()+" wins against "+player2.getName()+"**");
                            embed.addField("**"+player1.getName()+"** vs **"+player2.getName()+"**",Tools.getPlayer(player1).getBatShip1v1Stats(player2)+" - "+Tools.getPlayer(player2).getBatShip1v1Stats(player1),false);
                            player1 = null;
                            player2 = null;
                            this.event.getChannel().sendMessage(embed.build()).queue();
                            embed.clear();
                            return;
                        }
                    }
                    case 2,3 -> {
                        event.getChannel().sendMessage("That coordinate has already been shot! (Try again!)").queue();
                        return;
                    }
                }
                player2.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Your opponent shot at `"+message+"`.").queue());
                printMap(player2,false,event.getChannel());
                printMap(player2,true,player2.openPrivateChannel().complete());
                p1timer.cancel();
                turn = player2;
                p2timer.schedule(p2timeout,120000);
            }
            else if(turn.equals(player2)){
                switch(map1[row-1][column-1]){
                    case 0 -> {
                        map1[row-1][column-1] = 2;
                        event.getChannel().sendMessage("Miss!").queue();
                    }
                    case 1 -> {
                        map1[row-1][column-1] = 3;
                        event.getChannel().sendMessage("Hit!").queue();
                        boolean allSunk = true;
                        for(int[]arr:map1){
                            if(Arrays.toString(arr).contains("1")){
                                allSunk = false;
                                break;
                            }
                        }
                        if(allSunk){
                            printMap(player2,true,player1.openPrivateChannel().complete());
                            printMap(player1,true,event.getChannel());
                            event.getChannel().sendMessage("You win!").queue();
                            player1.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You lost!").queue());
                            Main.inGame.remove(player1);
                            Main.inGame.remove(player2);
                            Tools.getPlayer(player2).winBatShip(player1);
                            Tools.getPlayer(player1).loseBatShip();
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle("Results");
                            embed.setColor(0x00e5ff);
                            embed.setDescription("**"+player1.getName()+" loses to "+player2.getName()+"**");
                            embed.addField("**"+player1.getName()+"** vs **"+player2.getName()+"**",Tools.getPlayer(player1).getBatShip1v1Stats(player2)+" - "+Tools.getPlayer(player2).getBatShip1v1Stats(player1),false);
                            player1 = null;
                            player2 = null;
                            this.event.getChannel().sendMessage(embed.build()).queue();
                            embed.clear();
                            return;
                        }
                    }
                    case 2,3 -> {
                        event.getChannel().sendMessage("That coordinate has already been shot! (Try again!)").queue();
                        return;
                    }
                }
                player1.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Your opponent shot at `"+message+"`.").queue());
                printMap(player1,false,event.getChannel());
                printMap(player1,true,player1.openPrivateChannel().complete());
                p2timer.cancel();
                turn = player1;
                p1timer.schedule(p1timeout,120000);
            }
            turn.openPrivateChannel().queue((privateChannel -> privateChannel.sendMessage("Your turn!").queue()));
        }
    }
    public void printMap(User player, boolean seeShipLocations, PrivateChannel channel){
        int[][]board = map2;
        if(player.equals(player1)){
            board = map1;
        }
        String mapAsString = ":blue_square::one::two::three::four::five::six::seven::eight::nine::keycap_ten:";
        for(int i=0;i<10;i++){
            mapAsString+="\n:regional_indicator_"+(char)(i+97)+":";
            for(int j=0;j<10;j++){
                if(board[i][j]==0){
                    mapAsString+=":blue_square:";
                }
                else if(board[i][j]==1&&seeShipLocations){
                    mapAsString+=":black_large_square:";
                }
                else if(board[i][j]==1&&!seeShipLocations){
                    mapAsString+=":blue_square:";
                }
                else if(board[i][j]==2){
                    mapAsString+=":white_circle:";
                }
                else if(board[i][j]==3&&seeShipLocations){
                    mapAsString+=":x:";
                }
                else if(board[i][j]==3&&!seeShipLocations){
                    mapAsString+=":red_square:";
                }
            }
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(player.getName()+"'s Board:");
        embed.setDescription(mapAsString);
        channel.sendMessage(embed.build()).queue();
    }
}

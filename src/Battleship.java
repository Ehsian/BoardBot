import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class Battleship extends ListenerAdapter {

    boolean reactable;

    GuildMessageReceivedEvent event;
    String[]args;
    User player1;
    User player2;
    User turn;
    boolean gameState; //false == setup, true == battle

    int[][]map1 = new int[10][10];
    int[][]map2 = new int[10][10];

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
            /*
            player1.openPrivateChannel().queue((privateChannel -> {
                privateChannel.sendMessage("React your selection!").queue(message -> {
                    message.addReaction("\uD83E\uDEA8").queue();
                    message.addReaction("📄").queue();
                    message.addReaction("✂").queue();
                });
            }));
            player2.openPrivateChannel().queue((privateChannel -> {
                privateChannel.sendMessage("React your selection!").queue(message -> {
                    message.addReaction("\uD83E\uDEA8").queue();
                    message.addReaction("📄").queue();
                    message.addReaction("✂").queue();
                });
            }));
            */
        }
        else if(event.getReactionEmote().getName().equals("❎")&&event.getMember().getUser().equals(player2)&&reactable){
            reactable = false;
            event.getChannel().sendMessage("Challenge declined.").queue();
            Main.inGame.remove(player1);
        }
    }
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event){
        String message = event.getMessage().getContentRaw().toLowerCase();
        if(!gameState){
            //to be implemented (set up ships)
        }
        else if(gameState){
            //to be implemented (battle)
            if(Tools.toInteger(message.charAt(0))==-1){
                event.getChannel().sendMessage("Please enter a valid row. (a-j)");
                return;
            }
            try{
                if(Integer.parseInt(message.substring(1))>10){
                    event.getChannel().sendMessage("Please enter a valid column. (1-10)");
                    return;
                }
            } catch(Exception e){
                event.getChannel().sendMessage("Please send coordinates in a valid format. (row letter followed by column number)");
                event.getChannel().sendMessage("E.g. `f8` or `f 8`");
                return;
            }
        }
    }
}

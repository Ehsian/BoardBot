import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Timer;
import java.util.TimerTask;

public class RockPaperScissors extends ListenerAdapter {
    GuildMessageReceivedEvent event;
    String[]args;
    public RockPaperScissors(String[] args, GuildMessageReceivedEvent event){
        this.args = args;
        this.event = event;
        main();
    }
    User player1;
    int player1choice = -1; //0 rock, 1 paper, 2, scissors
    User player2;
    int player2choice = -1;
    boolean reactable;
    boolean singleplayer;
    boolean multiplayer;
    TimerTask terminate1 = new TimerTask(){
        @Override
        public void run(){
            if(reactable){
                Main.inGame.remove(player1);
                reactable = false;
                event.getChannel().sendMessage("Operation terminated. (Timeout)").queue();
            }
            else if(singleplayer){
                Main.inGame.remove(player1);
                player1.openPrivateChannel().queue((dm -> dm.sendMessage("Operation terminated. (Timeout)").queue()));
                event.getChannel().sendMessage("Operation terminated. (Timeout)").queue();
                player1 = null;
            }
        }
    };
    TimerTask terminate2 = new TimerTask(){
        @Override
        public void run(){
            if(multiplayer){
                Main.inGame.remove(player1);
                Main.inGame.remove(player2);
                player1.openPrivateChannel().queue((dm -> dm.sendMessage("Operation terminated. (Timeout)").queue()));
                event.getChannel().sendMessage("Operation terminated. (Timeout)").queue();
                player1 = null;
                player2.openPrivateChannel().queue((dm -> dm.sendMessage("Operation terminated. (Timeout)").queue()));
                player2 = null;
            }
        }
    };
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
                multiplayer = true;
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
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) throws NullPointerException,IllegalStateException{
        if(event.getReactionEmote().getName().equals("✅")&&event.getMember().getUser().equals(player2)&&reactable){
            if(Main.inGame.contains(player2)){
                event.getChannel().sendMessage("Challenge failed. (Opponent is already in a game.)").queue();
                player2 = null;
                return;
            } else{
                Main.inGame.add(player2);
            }
            //player2choice=-1;
            reactable = false;
            singleplayer = false;
            event.getChannel().sendMessage("Commencing game... (Check your DMs)").queue();
            player1.openPrivateChannel().queue((privateChannel -> {
                privateChannel.sendMessage("React your selection!").queue(message -> {
                    message.addReaction("\uD83E\uDEA8").queue();
                    message.addReaction("📄").queue();
                    message.addReaction("✂").queue();
                });
            }));
            Timer timer1 = new Timer();
            timer1.schedule(terminate2,15000);
            player2.openPrivateChannel().queue((privateChannel -> {
                privateChannel.sendMessage("React your selection!").queue(message -> {
                    message.addReaction("\uD83E\uDEA8").queue();
                    message.addReaction("📄").queue();
                    message.addReaction("✂").queue();
                });
            }));
        }
        else if(event.getReactionEmote().getName().equals("❎")&&event.getMember().getUser().equals(player2)&&reactable){
            reactable = false;
            event.getChannel().sendMessage("Challenge declined.").queue();
            Main.inGame.remove(player1);
        }
    }
    public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event)throws NullPointerException{
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Results");
        embed.setColor(0x00e5ff);
        if(event.getUser().equals(player1)){
            if(event.getReactionEmote().getName().equals("\uD83E\uDEA8")){
                player1choice = 0;
            } else if(event.getReactionEmote().getName().equals("📄")){
                player1choice = 1;
            } else if(event.getReactionEmote().getName().equals("✂")){
                player1choice = 2;
            }
            if(singleplayer){
                singleplayer = false;
            }
        }
        else if(event.getUser().equals(player2)){
            if(event.getReactionEmote().getName().equals("\uD83E\uDEA8")){
                player2choice = 0;
            } else if(event.getReactionEmote().getName().equals("📄")){
                player2choice = 1;
            } else if(event.getReactionEmote().getName().equals("✂")){
                player2choice = 2;
            }
        }
        if(player1choice!=-1&&player2choice!=-1){
            //----------------------------------------------------------------- For Player 1
            if(player2choice==0){
                player1.openPrivateChannel().queue((dm -> dm.sendMessage("\uD83E\uDEA8").queue()));
            } else if(player2choice==1){
                player1.openPrivateChannel().queue((dm -> dm.sendMessage("📄").queue()));
            } else{
                player1.openPrivateChannel().queue((dm -> dm.sendMessage("✂").queue()));
            }
            if(player2choice==player1choice+1||player2choice==0&&player1choice==2){
                player1.openPrivateChannel().queue((dm -> dm.sendMessage("You lost!").queue()));
                embed.setDescription("**"+player1.getName()+" loses to "+player2.getName()+"**");
                Tools.getPlayer(player1).loseRps();
                Tools.getPlayer(player2).winRps(player1);
            } else if(player2choice==player1choice){
                player1.openPrivateChannel().queue((dm -> dm.sendMessage("Draw.").queue()));
                embed.setDescription("**"+player1.getName()+" draws against "+player2.getName()+"**");
                Tools.getPlayer(player1).totalgamesplayed++;
                Tools.getPlayer(player2).totalgamesplayed++;
            } else{
                player1.openPrivateChannel().queue((dm -> dm.sendMessage("You win!").queue()));
                embed.setDescription("**"+player1.getName()+" wins against "+player2.getName()+"**");
                Tools.getPlayer(player1).winRps(player2);
                Tools.getPlayer(player2).loseRps();
            }

            //----------------------------------------------------------------- For Player 2/BoardBot
            if(multiplayer){
                multiplayer = false;
                Main.inGame.remove(player2);
                if(player1choice==0){
                    player2.openPrivateChannel().queue((dm -> dm.sendMessage("\uD83E\uDEA8").queue()));
                } else if(player1choice==1){
                    player2.openPrivateChannel().queue((dm -> dm.sendMessage("📄").queue()));
                } else{
                    player2.openPrivateChannel().queue((dm -> dm.sendMessage("✂").queue()));
                }
                if(player1choice==player2choice+1||player1choice==0&&player2choice==2){
                    player2.openPrivateChannel().queue((dm -> dm.sendMessage("You lost!").queue()));
                } else if(player1choice==player2choice){
                    player2.openPrivateChannel().queue((dm -> dm.sendMessage("Draw.").queue()));
                } else{
                    player2.openPrivateChannel().queue((dm -> dm.sendMessage("You win!").queue()));
                }
            }
            embed.addField("**"+player1.getName()+"** vs **"+player2.getName()+"**",Tools.getPlayer(player1).getRps1v1Stats(player2)+" - "+Tools.getPlayer(player2).getRps1v1Stats(player1),false);
            Main.inGame.remove(player1);
            player1 = null;
            player1choice = -1;
            player2 = null;
            player2choice = -1;
            this.event.getChannel().sendMessage(embed.build()).queue();
            embed.clear();
        }
    }
}
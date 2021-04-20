import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Tools {
    public static Player getPlayer(User user){
        return Main.allPlayers.get(Main.allUsers.indexOf(user));
    }
    public static boolean isValidChallenge(User player1, User player2, GuildMessageReceivedEvent event){
        if(Main.inGame.contains(player1)){
            event.getChannel().sendMessage("Challenge failed. (Finish your current game first!)").queue();
            return false;
        }
        else if(player2.isBot()||player2.equals(player1)) {
            event.getChannel().sendMessage("Please challenge a valid user.").queue();
            return false;
        }
        else if(Main.inGame.contains(player2)){
            event.getChannel().sendMessage("Challenge failed. (Opponent is already in a game.)").queue();
            return false;
        }
        return true;
    }
}

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Tools {
    public static Player getPlayer(User user){
        return Main.allPlayers.get(Main.allUsers.indexOf(user));
    }
    public static boolean isValidChallenge(User player1, GuildMessageReceivedEvent event){
        if(Main.inGame.contains(player1)){
            event.getChannel().sendMessage("Challenge failed. (Finish your current game first!)").queue();
            return false;
        }
        return true;
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
    public static int toInteger(char ch){
        return switch (ch) {
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            case 'i' -> 9;
            case 'j' -> 10;
            default -> -1;
        };
    }
    public static User firstTurn(User p1, User p2){
        if((int)(Math.random()*2)==0){
            return p1;
        }
        return p2;
    }
}

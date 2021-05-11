import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Battleship {
    GuildMessageReceivedEvent event;
    String[]args;

    User player1;
    User player2;
    public Battleship(String[]args, GuildMessageReceivedEvent event){
        this.event = event;
        this.args = args;
    }
    public void main(){
        player1 = event.getAuthor();
        player2 = Main.jda.getSelfUser();

    }
}

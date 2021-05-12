import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Ping {
    public static void main(GuildMessageReceivedEvent event) {
        long time = System.currentTimeMillis();
        event.getChannel().sendMessage("Pong!").queue(response -> response.editMessageFormat("Pong! %dms", (System.currentTimeMillis()-time)/2).queue());
    }
}

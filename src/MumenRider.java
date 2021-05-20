import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MumenRider extends ListenerAdapter {
    public MumenRider(GuildMessageReceivedEvent event){
        event.getChannel().sendMessage("**JUSTICE CRASH!**").queue();
    }
}

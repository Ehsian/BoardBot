import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.io.*;
import java.util.*;

public class Help extends ListenerAdapter{
    static String help1 = "`help` - You're already here!\n" +
            "`ping` - Finds latency of the bot.\n" +
            "`rps <user>` - Challenges a user to a duel of rock paper scissors. Omit <user> to challenge the bot.\n" +
            "`battleship <user>` - Challenges a user to a duel of Battleship.\n" +
            "`leaderboard <category>` - Displays the top users of the event specified. Omit <category> to see all categories.";
    public static void main(String[] args, GuildMessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Help");
        embed.setDescription("Page 1 - Click the arrows for more commands.");
        embed.addField("General Commands",help1,false);
        embed.setFooter("Bot created by Ehsian#7062", Objects.requireNonNull(event.getMember()).getUser().getAvatarUrl());
        embed.setColor(0x00e5ff);
        event.getChannel().sendMessage(embed.build()).queue();
        embed.clear();
    }
}

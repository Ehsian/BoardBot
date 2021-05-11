import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashSet;

public class Main extends ListenerAdapter {

    public static JDA jda;
    public static String prefix = "~";
    public static HashSet<User>inGame = new HashSet<>();
    public static ArrayList<User> allUsers = new ArrayList<>();
    public static ArrayList<Player> allPlayers = new ArrayList<>();

    public static void main(String[] args) throws LoginException,IOException {
        //-------------------------------------------------- Initialize Bot
        BufferedReader br = new BufferedReader(new FileReader("ignore/token.txt"));
        jda = JDABuilder.createDefault(br.readLine()).build();
        br.close();
        if(!allUsers.contains(jda.getSelfUser())){
            allUsers.add(jda.getSelfUser());
            allPlayers.add(new Player(jda.getSelfUser().getId()));
        }

        //-------------------------------------------------- Set Status
        jda.getPresence().setActivity(Activity.watching(String.format("for commands | %shelp",prefix)));
        jda.addEventListener(new Main());
    }

    //------------------------------------------------------ Command Listener
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String[] args = event.getMessage().getContentRaw().split(" ");
        if(args[0].length()>Main.prefix.length()&&args[0].substring(0,Main.prefix.length()).equalsIgnoreCase(Main.prefix)){
            if(!allUsers.contains(event.getAuthor())){
                allUsers.add(event.getAuthor());
                allPlayers.add(new Player(event.getAuthor().getId()));
            }
            if(args.length>1){
                try{
                    User temp = event.getMessage().getMentionedUsers().get(0);
                    if(!temp.isBot()&&!temp.equals(event.getAuthor())
                            &&!allUsers.contains(temp)){
                        allUsers.add(event.getMessage().getMentionedUsers().get(0));
                        allPlayers.add(new Player(event.getMessage().getMentionedUsers().get(0).getId()));
                    }
                }
                catch(Exception e){}
            }
            switch (args[0].substring(Main.prefix.length()).toLowerCase()) {
                case "help", "info", "commands" -> Help.main(args, event);
                case "ping", "latency" -> Ping.main(event);
                case "rps", "rockpaperscissors" -> {
                    if(args.length>1){
                        jda.addEventListener(new RockPaperScissors(args, event));
                    }
                    else{
                        jda.addEventListener(new RPSsingle(args, event));
                    }
                }
                case "battleship" -> jda.addEventListener(new Battleship(args,event));
                case "test" -> {
                    try {
                        SaveData.saveData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case "test2" -> {
                    try {
                        SaveData.loadData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "test3" -> System.out.println(Tools.getPlayer(event.getAuthor()).totalgamesplayed);
            }
        }
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event){

    }
}
package PG;

import arc.Core;
import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.entities.type.Player;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.plugin.Plugin;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static mindustry.Vars.*;

public class Main extends Plugin {
    private JSONObject adata;
    public static JSONObject badList;

    public Main() throws InterruptedException {
        try {
            String pureJson = Core.settings.getDataDirectory().child("mods/settings.json").readString();
            adata = new JSONObject(new JSONTokener(pureJson));
            if (!adata.has("badList")){
                Log.err("============");
                Log.err("ERROR - 404");
                Log.err("settings.json missing badList");
                Log.err("Make sure settings.json is properly setup");
                Log.err("============");
                return;
            } else {
                badList = adata.getJSONObject("badList");
            }
        } catch (Exception e) {
            if (e.getMessage().contains("File not found: config\\mods\\settings.json")){
                Log.err("PG: settings.json file is missing.");
                return;
            } else {
                Log.err("PG: Error reading JSON.");
                e.printStackTrace();
                return;
            }
        }
        Events.on(EventType.ServerLoadEvent.class, event -> {
            netServer.admins.addChatFilter((player, text) -> null);
        });
        Events.on(EventType.PlayerChatEvent.class, event -> {
            Player player = event.player;
            if (!event.message.startsWith("/")) {
                Call.sendMessage("[coral][[[#"+player.color + "]" + player.name + "\uE95C[coral]]: [white]" + byteCode.censor(event.message));
                Log.info(player.name + " : " + event.message);
            }
        });

    }
    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("bla", "<word>", "adds Bad Word to badList censor", arg -> {
            JSONObject badlist = adata.getJSONObject("badList");
            if (badlist.has(arg[0])) {
                Log.err("badList already contains `{0}`!", arg[0]);
            } else {
                badlist.put(arg[0], "bad");
            }
            try {
                File file = new File("config\\mods\\settings.json");
                FileWriter out = new FileWriter(file, false);
                PrintWriter pw = new PrintWriter(out);
                pw.println(adata.toString(4));
                out.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
            Log.info("Successfully added {0} to badList!", arg[0]);
        });
        handler.register("blr", "<word>", "remover Word to badList censor", arg -> {
            JSONObject badlist = adata.getJSONObject("badList");
            if (!badlist.has(arg[0])) {
                Log.err("badList does not contain `{0}`!", arg[0]);
            } else {
                badlist.remove(arg[0]);
            }
            try {
                File file = new File("config\\mods\\settings.json");
                FileWriter out = new FileWriter(file, false);
                PrintWriter pw = new PrintWriter(out);
                pw.println(adata.toString(4));
                out.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
            Log.info("Successfully removed {0} to badList!", arg[0]);
        });
    }
}
package pl.itemszop;

import com.sun.tools.javac.Main;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

public class FirebaseSync {

    private static FirebaseSync instance;
    final HttpClient client = HttpClient.newHttpClient();

    public static FirebaseSync getInstance() {
        return instance;
    }
    public void syncWithFirebase(){
        try {
            JSONObject commands = getCommands();
            if(commands!=null){
                Set<?> keys = commands.keySet();
                keys.forEach((key) -> {
                    String commandId = key.toString();
                    String command = commands.get(key.toString()).toString();
                    Itemszop.getInstance().getLogger().info(command);
                    Itemszop.getInstance().getServer().dispatchCommand(Itemszop.getInstance().getServer().getConsoleSender(), command);
                    deleteCommand(commandId);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getCommands() throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Settings.IMP.OPTIONS.DATABASEURL + "/servers/" + Settings.IMP.OPTIONS.SERVERID + "/commands.json"))
                .header("Accept", "application/json")
                .build();
        String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(response);
    }

    private void deleteCommand(String commandId){
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Settings.IMP.OPTIONS.DATABASEURL + "/servers/" + Settings.IMP.OPTIONS.SERVERID + "/commands/" + commandId + ".json"))
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
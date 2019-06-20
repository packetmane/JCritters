package com.jcritters;

import com.jcritters.handlers.Login;
import com.jcritters.handlers.Navigation;
import com.jcritters.handlers.Player;
import com.playfab.PlayFabSettings;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.json.JSONArray;
import java.util.Properties;
import org.json.JSONObject;

/**
 *
 * @author packetmane
 */
public class World {
    private ArrayList<CritterWebSocket> critterWebSockets = new ArrayList<>();
    private Map<String, BiConsumer<CritterWebSocket, JSONObject>> worldHandlers = new HashMap<>();
    
    public World() {
        try {
            Properties configProperties = new Properties();
            String configPropertiesFile = "/com/jcritters/resources/config.properties";
            InputStream configInputStream = getClass().getClassLoader().getResourceAsStream(configPropertiesFile);

            configProperties.load(configInputStream);

            PlayFabSettings.TitleId = configProperties.getProperty("PlayFabTitleId");
            PlayFabSettings.DeveloperSecretKey = configProperties.getProperty("PlayFabDeveloperSecretKey");

            this.worldHandlers.put("login", (critterWebSocket, messageJSONObject) -> Login.handle(critterWebSocket, messageJSONObject));
            this.worldHandlers.put("joinRoom", (critterWebSocket, messageJSONObject) -> Navigation.joinRoom(critterWebSocket, messageJSONObject));
            this.worldHandlers.put("click", (critterWebSocket, messageJSONObject) -> Player.movePlayer(critterWebSocket, messageJSONObject));
            this.worldHandlers.put("sendMessage", (critterWebSocket, messageJSONObject) -> Player.sendMessage(critterWebSocket, messageJSONObject));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public void handleMessage(CritterWebSocket critterWebSocket, String message) {
        char messageValue = message.charAt(0);
        
        switch(messageValue) {
            case '4':
                JSONArray messageData = new JSONArray(message.substring(2));
                String messageId = messageData.get(0).toString();
                String messageJSON = messageData.get(1).toString();

                if(this.worldHandlers.containsKey(messageId)) {
                    // If not a "login" command, then check if the user is logged in before handling it.
                    boolean handleCritterWebSocket = false;
                    
                    if(messageId.equals("login")) {
                        handleCritterWebSocket = true;
                    } else if(critterWebSocket.isLoggedIn()) {
                        handleCritterWebSocket = true;
                    }
                    
                    if(handleCritterWebSocket) {
                        JSONObject messageJSONObject = new JSONObject(messageJSON);
                        
                        this.worldHandlers.get(messageId).accept(critterWebSocket, messageJSONObject);
                    } else {
                        // Trying to use successful-login-required handlers, while not logged in.
                        critterWebSocket.close();
                    }
                } else {
                    System.out.println("World handler can't been identified - " + messageId);
                }
                break;
            
            case '2':
                // Pong
                critterWebSocket.send("3");
                break;
            
            default:
                // Any other messageValue is an invalid message.
                critterWebSocket.close();
                break;
        }
    }
    
    public void addCritterWebSocketToWorld(CritterWebSocket critterWebSocket) {
        this.critterWebSockets.add(critterWebSocket);
    }
    
    public void removeCritterWebSocketFromWorld(CritterWebSocket critterWebSocket) {
        this.critterWebSockets.remove(critterWebSocket);
    }
}

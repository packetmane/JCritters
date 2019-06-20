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

/**
 *
 * @author packetmane
 */
public class World {
    private static final ArrayList<CritterWebSocket> critterWebSockets = new ArrayList<>();
    private Map<String, BiConsumer<CritterWebSocket, String>> worldHandlers = new HashMap<>();
    private static final Map<String, Room> rooms = new HashMap<>();
    
    public World() {
        Properties configProperties = new Properties();
	String configPropertiesFile = "/com/jcritters/resources/config.properties";
        InputStream configInputStream = getClass().getClassLoader().getResourceAsStream(configPropertiesFile);
        
        try {
            configProperties.load(configInputStream);
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        PlayFabSettings.TitleId = configProperties.getProperty("PlayFabTitleId");
        PlayFabSettings.DeveloperSecretKey = configProperties.getProperty("PlayFabDeveloperSecretKey");
        
        this.worldHandlers.put("login", (critterWebSocket, message) -> Login.handle(critterWebSocket, message));
        this.worldHandlers.put("joinRoom", (critterWebSocket, message) -> Navigation.joinRoom(critterWebSocket, message));
        this.worldHandlers.put("click", (critterWebSocket, message) -> Player.movePlayer(critterWebSocket, message));
        this.worldHandlers.put("sendMessage", (critterWebSocket, message) -> Player.sendMessage(critterWebSocket, message));
        
        // Work on a rooms configuration file, to easily load all rooms.
        this.rooms.put("bridge", new Room("bridge", "Bridge", 850, 600, 2, "{\"background\":\"/media/rooms/bridge_bg.png\",\"sprites\":{\"images\":[\"/media/rooms/bridge_ss.png\"],\"frames\":[[1,1,86,226,0,36,217,645,378],[89,1,67,91,0,40,52,454,209],[158,1,88,66,0,44,53,501,356],[89,94,74,69,0,54,51,274,221],[89,165,80,48,0,15,29,349,177],[165,69,43,83,0,23,57,535,271],[210,69,76,41,0,54,31,364,342],[210,112,60,65,0,37,43,295,317],[171,179,54,64,0,26,35,376,316],[227,179,52,60,0,27,36,313,286]]}}"));
        this.rooms.put("tavern", new Room("tavern", "Tavern", 850, 600, 2, "{\"background\":\"/media/rooms/tavern_bg.png\",\"foreground\":\"/media/rooms/tavern_fg.png\",\"sprites\":{\"images\":[\"/media/rooms/tavern_ss.png\"],\"frames\":[[577,161,170,61,0,85,42,455,180],[221,0,147,240,0,74,118,314,201],[625,0,181,161,0,90,52,301,341],[806,0,127,118,0,64,103,259,434],[0,0,221,174,0,110,133,670,245],[235,240,42,38,0,21,1,665,274],[353,242,37,35,0,18,3,592,252],[316,240,37,35,0,18,2,548,208],[933,0,83,151,0,31,122,830,245],[719,222,68,63,0,24,29,837,279],[195,240,40,40,0,20,2,484,307],[277,240,39,40,0,20,2,339,320],[0,174,103,78,0,52,49,409,336],[103,174,68,88,0,34,38,563,380],[965,239,53,33,0,26,4,430,415],[171,174,24,68,0,12,59,411,442],[906,151,118,88,0,59,37,496,399],[368,137,128,105,0,64,19,812,309],[368,0,257,137,0,129,48,722,357],[496,137,81,138,0,40,94,28,213],[806,118,100,147,0,70,111,12,292],[577,222,65,84,0,32,44,9,331],[779,161,22,34,0,11,23,162,246],[747,161,32,57,0,16,15,85,250],[642,222,77,64,0,39,45,127,204],[906,239,59,68,0,29,13,205,221]]}}"));
    }
    
    public void handleMessage(CritterWebSocket critterWebSocket, String message) {
        char messageValue = message.charAt(0);
        
        switch(messageValue) {
            case '4':
                JSONArray messageData = new JSONArray(message.substring(2));
                String messageId = messageData.get(0).toString();
                String messageJSON = messageData.get(1).toString();

                if(this.worldHandlers.containsKey(messageId)) {
                    // If not a login command, then check if the user is logged in before handling it.
                    boolean handleCritterWebSocket = false;
                    
                    if(messageId.equals("login")) {
                        handleCritterWebSocket = true;
                    } else if(critterWebSocket.isLoggedIn()) {
                        handleCritterWebSocket = true;
                    }
                    
                    if(handleCritterWebSocket) {
                        this.worldHandlers.get(messageId).accept(critterWebSocket, messageJSON);
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
    
    public ArrayList<CritterWebSocket> getCritterWebSockets() {
        return this.critterWebSockets;
    }
    
    public static Map<String, Room> getRooms() {
        return rooms;
    }
}

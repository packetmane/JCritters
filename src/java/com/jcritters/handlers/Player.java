package com.jcritters.handlers;

import com.jcritters.CritterWebSocket;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author packetmane
 */
public class Player {
    public static void movePlayer(CritterWebSocket critterWebSocket, String message) {
        JSONObject messageJSONObject = new JSONObject(message);
        int x = messageJSONObject.getInt("x");
        int y = messageJSONObject.getInt("y");
        int positionAngle = (int) ((180 / Math.PI) * - Math.atan2(x - critterWebSocket.getX(), y - critterWebSocket.getY()) + 180); // ?
        Map<String, Object> positionData = new HashMap<>();
        
        positionData.put("i", critterWebSocket.getId());
        positionData.put("x", x);
        positionData.put("y", y);
        
        JSONObject positionDataJSONObject = new JSONObject(positionData);
        
        critterWebSocket.setX(x);
        critterWebSocket.setY(y);
        critterWebSocket.setRotation(positionAngle);
        critterWebSocket.getRoom().send("X", positionDataJSONObject);
    }
    
    public static void sendMessage(CritterWebSocket critterWebSocket, String message) {
        JSONObject messageJSONObject = new JSONObject(message);
        String playerMessage = messageJSONObject.getString("message");
        
        // Move "Commands" to a plugin, comment this out if "Commands" should be disabled.
        if(playerMessage.charAt(0) == '/') {
            String[] playerMessageSplit = playerMessage.split(" ");
            
            switch(playerMessageSplit[0]) {
                case "/join":
                    critterWebSocket.joinRoom(playerMessageSplit[1]);
                    return;
            }
        }
        
        // Send message
        Map<String, String> messageData = new HashMap<>();
        
        messageData.put("i", critterWebSocket.getId());
        messageData.put("n", critterWebSocket.getNickname());
        messageData.put("m", playerMessage);
        
        JSONObject messageDataJSONObject = new JSONObject(messageData);
        
        critterWebSocket.getRoom().send("M", messageDataJSONObject);
    }
}

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
    public void movePlayer(CritterWebSocket critterWebSocket, JSONObject messageJSONObj) {
        int x = messageJSONObj.getInt("x");
        int y = messageJSONObj.getInt("y");
        int positionAngle = (int) ((180 / Math.PI) * - Math.atan2(x - critterWebSocket.getX(), y - critterWebSocket.getY()) + 180); // ?
        Map<String, Object> positionData = new HashMap<>();
        
        positionData.put("i", critterWebSocket.getId());
        positionData.put("x", x);
        positionData.put("y", y);
        
        JSONObject positionDataJSONObj = new JSONObject(positionData);
        
        critterWebSocket.setX(x);
        critterWebSocket.setY(y);
        critterWebSocket.setRotation(positionAngle);
        critterWebSocket.getRoom().send("X", positionDataJSONObj);
    }
    
    public void sendMessage(CritterWebSocket critterWebSocket, JSONObject messageJSONObj) {
        String playerMessage = messageJSONObj.getString("message");
        
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
        
        JSONObject messageDataJSONObj = new JSONObject(messageData);
        
        critterWebSocket.getRoom().send("M", messageDataJSONObj);
    }
}

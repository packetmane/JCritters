package com.jcritters.handlers;

import com.jcritters.CritterWebSocket;
import com.jcritters.RoomManager;
import org.json.JSONObject;

/**
 * 
 * @author packetmane
 */
public class Navigation {
    public static void joinRoom(CritterWebSocket critterWebSocket, String message) {
        JSONObject messageJSONObject = new JSONObject(message);
        
        critterWebSocket.setX(433); // default X
        critterWebSocket.setY(195); // default Y
        critterWebSocket.setRotation(180); // default rotation
        RoomManager.getRoom(messageJSONObject.getString("roomId")).add(critterWebSocket);
    }
}

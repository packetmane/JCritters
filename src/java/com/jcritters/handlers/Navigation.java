package com.jcritters.handlers;

import com.jcritters.CritterWebSocket;
import com.jcritters.World;
import org.json.JSONObject;

/**
 * 
 * @author packetmane
 */
public class Navigation {
    public static void joinRoom(CritterWebSocket critterWebSocket, String message) {
        JSONObject messageJSONObject = new JSONObject(message);
        
        critterWebSocket.setX(350);
        critterWebSocket.setY(210);
        critterWebSocket.setRotation(180);
        World.getRooms().get(messageJSONObject.getString("roomId")).add(critterWebSocket);
    }
}

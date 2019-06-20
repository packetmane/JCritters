package com.jcritters.handlers;

import com.jcritters.CritterWebSocket;
import org.json.JSONObject;

/**
 * 
 * @author packetmane
 */
public class Navigation {
    public static void joinRoom(CritterWebSocket critterWebSocket, JSONObject messageJSONObject) {
        critterWebSocket.setX(433); // default X
        critterWebSocket.setY(195); // default Y
        critterWebSocket.setRotation(180); // default rotation
        critterWebSocket.getRoomManager().getRoom(messageJSONObject.getString("roomId")).add(critterWebSocket);
    }
}

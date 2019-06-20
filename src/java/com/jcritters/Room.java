package com.jcritters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author packetmane
 */
public class Room {
    private String id = null;
    private String name = null;
    private int width = 0;
    private int height = 0;
    private int version = 0;
    private ArrayList<CritterWebSocket> roomCritterWebSockets = new ArrayList<>();
    private String artwork = null; // should be a JSONObject
    
    public Room(String id, String name, int width, int height, int version, String artwork) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.version = version;
        this.artwork = artwork;
    }
    
    public void add(CritterWebSocket critterWebSocket) {
        System.out.println("Room:add(): " + critterWebSocket.getId());
        
        if(critterWebSocket.getRoom() != null) {
            critterWebSocket.getRoom().remove(critterWebSocket);
        }
        
        ArrayList<JSONObject> crittersList = new ArrayList<>();
        
        for(CritterWebSocket critterWebSocketObj : roomCritterWebSockets) {
            crittersList.add(critterWebSocketObj.getCritterDataJSONObject());
        }
        
        critterWebSocket.setRoom(this);
        this.roomCritterWebSockets.add(critterWebSocket);
        
        Map<String, Object> roomData = new HashMap<>();
        
        roomData.put("version", this.version);
        roomData.put("roomId", this.id);
        roomData.put("name", this.name);
        roomData.put("width", this.width);
        roomData.put("height", this.height);
        roomData.put("playerlist", new JSONArray(crittersList));
        roomData.put("artwork", new JSONObject(this.artwork));
        
        JSONObject roomDataJSONObject = new JSONObject(roomData);
        
        critterWebSocket.send("joinRoom", roomDataJSONObject);
        this.send("A", critterWebSocket.getCritterDataJSONObject());
    }
    
    public void remove(CritterWebSocket critterWebSocket) {
        System.out.println("Room:remove()`; " + critterWebSocket.getId());
        
        this.roomCritterWebSockets.remove(critterWebSocket);
        
        Map<String, String> critterData = new HashMap<>();
        
        critterData.put("i", critterWebSocket.getId());
        
        JSONObject critterDataJSONObject = new JSONObject(critterData);
        
        this.send("R", critterDataJSONObject);
    }
    
    public void send(String messageId, JSONObject messageDataJSONObject) {
        for(CritterWebSocket critterWebSocket : roomCritterWebSockets) {
            critterWebSocket.send(messageId, messageDataJSONObject);
        }
    }
}

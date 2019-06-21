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
    private JSONObject artwork = null;
    
    public Room(JSONObject roomJSONObj) {
        this.id = roomJSONObj.getString("id");
        this.name = roomJSONObj.getString("name");
        this.width = roomJSONObj.getInt("width");
        this.height = roomJSONObj.getInt("height");
        this.version = roomJSONObj.getInt("version");
        this.artwork = roomJSONObj.getJSONObject("artwork");
    }
    
    public void add(CritterWebSocket critterWebSocket) {
        System.out.println("Room:add(): " + critterWebSocket.getId());
        
        if(critterWebSocket.getRoom() != null) {
            critterWebSocket.getRoom().remove(critterWebSocket);
        }
        
        this.roomCritterWebSockets.add(critterWebSocket);
        
        ArrayList<JSONObject> crittersList = new ArrayList<>();
        
        for(CritterWebSocket critterWebSocketObj : roomCritterWebSockets) {
            crittersList.add(critterWebSocketObj.getCritterDataJSONObj());
        }
        
        Map<String, Object> roomData = new HashMap<>();
        
        roomData.put("version", this.version);
        roomData.put("roomId", this.id);
        roomData.put("name", this.name);
        roomData.put("width", this.width);
        roomData.put("height", this.height);
        roomData.put("playerlist", new JSONArray(crittersList));
        roomData.put("artwork", this.artwork);
        
        JSONObject roomDataJSONObj = new JSONObject(roomData);
        
        critterWebSocket.setRoom(this);
        critterWebSocket.send("joinRoom", roomDataJSONObj);
        this.send("A", critterWebSocket.getCritterDataJSONObj());
    }
    
    public void remove(CritterWebSocket critterWebSocket) {
        System.out.println("Room:remove(): " + critterWebSocket.getId());
        
        this.roomCritterWebSockets.remove(critterWebSocket);
        
        Map<String, String> critterData = new HashMap<>();
        
        critterData.put("i", critterWebSocket.getId());
        
        JSONObject critterDataJSONObj = new JSONObject(critterData);
        
        this.send("R", critterDataJSONObj);
    }
    
    public void send(String messageId, JSONObject messageDataJSONObj) {
        for(CritterWebSocket critterWebSocket : roomCritterWebSockets) {
            critterWebSocket.send(messageId, messageDataJSONObj);
        }
    }
    
    public String getId() {
        return this.id;
    }
}

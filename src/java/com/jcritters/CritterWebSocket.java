package com.jcritters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author packetmane
 */

@ServerEndpoint("/")
public class CritterWebSocket {
    private static final World crittersWorld = new World();
    private Session session = null;
    private String id = null;
    private String nickname = null;
    private int x = 0;
    private int y = 0;
    private int rotation = 0;
    private boolean loggedIn = false;
    private Room room = null;
    private String critterId = "hamster"; // ?
    
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("onOpen: " + session.getId());
        
        this.session = session;
        
        crittersWorld.addCritterWebSocketToWorld(this);
        
        Map<String, Object> openHandshake = new HashMap<>();
        
        openHandshake.put("sid", session.getId());
        openHandshake.put("upgrades", new JSONArray()); // empty JSONArray object for now
        openHandshake.put("pingInterval", "25000");
        openHandshake.put("pingTimeout", "5000");
        
        JSONObject handshakeJSONObject = new JSONObject(openHandshake);
        
        this.send("0" + handshakeJSONObject);
        this.send("40");
    }
    
    @OnClose
    public void onClose(Session session) {
        System.out.println("onClose: " + session.getId());
        
        crittersWorld.removeCritterWebSocketFromWorld(this);
        this.room.remove(this);
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("onMessage: " + session.getId());
        
        crittersWorld.handleMessage(this, message);
    }
    
    @OnError
    public void onError(Throwable t) {
        System.out.println("onError: " + t.getMessage());
    }
    
    public JSONObject getCritterDataJSONObject() {
        Map<String, Object> critterData = new HashMap<>();
        
        critterData.put("i", this.id);
        critterData.put("n", this.nickname);
        critterData.put("c", this.critterId);
        critterData.put("g", new JSONObject()); // empty JSONObject object for now
        critterData.put("x", this.x);
        critterData.put("y", this.y);
        critterData.put("r", this.rotation);
        critterData.put("s", 5); // ?
        
        JSONObject critterDataJSONObject = new JSONObject(critterData);
        
        return critterDataJSONObject;
    }
    
    public void send(String message) {
        if(this.session.isOpen()) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void send(String messageId, Object messageData) {
        if(this.session.isOpen()) {
            try {
                JSONArray messageJSONArray = new JSONArray();
                
                messageJSONArray.put(messageId);
                messageJSONArray.put(messageData);
                
                this.session.getBasicRemote().sendText("42" + messageJSONArray.toString());
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void joinRoom(String roomId) {
        this.room.add(this);
    }
    
    public ArrayList<CritterWebSocket> getCritterWebSockets() {
        return crittersWorld.getCritterWebSockets();
    }
    
    public void setCritterId(String critter) {
        this.critterId = critter;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
    
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    
    public void setRoom(Room room) {
        this.room = room;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getNickname() {
        return this.nickname;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getRotation() {
        return this.rotation;
    }
    
    public boolean isLoggedIn() {
        return this.loggedIn;
    }
    
    public Room getRoom() {
        return this.room;
    }
    
    public String getCritterId() {
        return this.critterId;
    }
    
    public void close() {
        try {
            this.session.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

package com.jcritters;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import org.json.JSONArray;
import org.json.JSONTokener;

/**
 *
 * @author packetname
 */

@Named
@ApplicationScoped
public class RoomManager {
    private ArrayList<Room> rooms = new ArrayList<>();
    
    public RoomManager() {
        try {
            File roomsJSONFile = new File(getClass().getResource("/com/jcritters/resources/json/rooms.json").getFile());
            FileReader jsonFileReader = new FileReader(roomsJSONFile);
            JSONTokener jsonTokener = new JSONTokener(jsonFileReader);
            JSONArray roomsJSONArray = new JSONArray(jsonTokener);
            
            for (int i = 0; i < roomsJSONArray.length(); i++) {
                Room room = new Room(roomsJSONArray.getJSONObject(i));
                
                this.rooms.add(room);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Room getRoom(String roomId) {
        Room room = null;
        
        for(Room roomObj : this.rooms) {
            if(roomObj.getId().equals(roomId)) {
                room = roomObj;
                
                break;
            }
        }
        
        return room;
    }
}

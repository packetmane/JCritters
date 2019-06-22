package com.jcritters.handlers;

import com.jcritters.CritterWebSocket;
import com.playfab.PlayFabServerAPI;
import com.playfab.PlayFabServerModels;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author packetmane
 */
public class Login {
    public void handle(CritterWebSocket critterWebSocket, JSONObject messageJSONObj) {
        PlayFabServerModels.AuthenticateSessionTicketRequest sessionTicketAuthenticationRequest = new PlayFabServerModels.AuthenticateSessionTicketRequest();
        
        sessionTicketAuthenticationRequest.SessionTicket = messageJSONObj.getString("ticket");
        String id = PlayFabServerAPI.AuthenticateSessionTicket(sessionTicketAuthenticationRequest).Result.UserInfo.PlayFabId;
        
        CritterWebSocket critterWebSocketObj = critterWebSocket.getWorld().getCritterById(id);
        
        if(critterWebSocketObj != null) {
            // Disconnect the other session.
            critterWebSocketObj.close();
        }
        
        critterWebSocket.setId(id);
        
        PlayFabServerModels.GetPlayerProfileRequest getPlayerProfileRequest = new PlayFabServerModels.GetPlayerProfileRequest();
        
        getPlayerProfileRequest.PlayFabId = id;
        
        String nickname = PlayFabServerAPI.GetPlayerProfile(getPlayerProfileRequest).Result.PlayerProfile.DisplayName;
        
        critterWebSocket.setNickname(nickname);
        critterWebSocket.send("beep", "test");
        
        Map<String, Object> loginData = new HashMap<>();
        
        loginData.put("playerId", id);
        loginData.put("critterId", critterWebSocket.getCritterId());
        loginData.put("nickname", nickname);
        loginData.put("inventory", new JSONArray()); // empty JSONArray object for now
        loginData.put("gear", new JSONObject()); // empty JSONObject object for now
        
        JSONObject loginDataJSONObj = new JSONObject(loginData);
        
        critterWebSocket.send("login", loginDataJSONObj);
        critterWebSocket.setLoggedIn();
    }
}

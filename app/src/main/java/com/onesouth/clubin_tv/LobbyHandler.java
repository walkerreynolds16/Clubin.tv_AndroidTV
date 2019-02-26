package com.onesouth.clubin_tv;

public class LobbyHandler {

    private static Lobby lobby = null;

    public static synchronized Lobby getLobby(String lobbyCode){
        if(lobby == null){
            lobby = new Lobby(lobbyCode);
        }

        return lobby;
    }
}

//MIT License
//
//        Copyright (c) 2021 Jacob Cox
//
//        Permission is hereby granted, free of charge, to any person obtaining a copy
//        of this software and associated documentation files (the "Software"), to deal
//        in the Software without restriction, including without limitation the rights
//        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//        copies of the Software, and to permit persons to whom the Software is
//        furnished to do so, subject to the following conditions:
//
//        The above copyright notice and this permission notice shall be included in all
//        copies or substantial portions of the Software.
//
//        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//        SOFTWARE.

package uk.co.jcox.votemod.util;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerFetcher implements Runnable {

    //todo This class needs re-writing

    private static final Map<String, UUID> cache = new HashMap<>();

    private static final String LINK = "https://api.mojang.com/users/profiles/minecraft/";
    private final String playerName;
    private final CompletableFuture<UUID> cf;
    private final TextSystem tx;

    public PlayerFetcher(String playerName, CompletableFuture<UUID> cf, TextSystem tx) {
        this.playerName = playerName;
        this.cf = cf;
        this.tx = tx;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("VoteMod/PlayerFetcher");
        tx.debugMessage("PlayerFetcher thread has started");

        if(cache.containsKey(playerName)) {
            cf.complete(cache.get(playerName));
            tx.debugMessage("Found " + playerName + " in the cache");
            return;
        }

        if(Bukkit.getPlayer(playerName) != null) {
            UUID playerUuid = Bukkit.getPlayer(playerName).getUniqueId();
            cache.put(playerName, playerUuid);
            cf.complete(playerUuid);
            tx.debugMessage("Found " + playerName + " in the list of online players");
            return;
        }


        Scanner scanner = null;
        try{
            URL url = new URL(LINK + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            tx.debugMessage("Connecting to Mojang: " + LINK + playerName);

            if(connection.getResponseCode() != 200) {
                tx.debugMessage("Error: " + connection.getResponseCode() + ". Cancelling request");
                cf.cancel(true);
                return;
            }

            if(connection.getResponseCode() == 204) {
                tx.debugMessage("Content not found");
                cf.cancel(true);
                return;
            }

            StringBuilder response = new StringBuilder();
            scanner = new Scanner(url.openStream());

            while(scanner.hasNext()) {
                response.append(scanner.nextLine());
            }

            JSONParser parse = new JSONParser();
            JSONObject data = (JSONObject) parse.parse(response.toString());

            UUID uuid = UUID.fromString(getFullUUID((String) data.get("id")));
            tx.debugMessage("Built UUID from player " + playerName + " as: " + uuid);
            cache.put(playerName, uuid);
            cf.complete(uuid);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                tx.debugMessage("Closing Scanner");
                scanner.close();
            }
        }
    }

    private static String getFullUUID(String uuid) {
        String i = uuid.substring(0, 8);
        i += "-";
        String j = uuid.substring(8, 12);
        j += "-";
        String k = uuid.substring(12, 16);
        k += "-";
        String l = uuid.substring(16, 20);
        l += "-";
        String rest = uuid.substring(20);
        return i + j  + k + l + rest;
    }

    public static void saveCache() {
        //This method may be implemented to write the cache to a file
    }
}
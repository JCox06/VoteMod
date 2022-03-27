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
import uk.co.jcox.votemod.Main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerFetcher implements Runnable {


    private static final String LINK = "https://api.mojang.com/users/profiles/minecraft/";
    private final String playerName;
    private final Main plugin;
    private final TextSystem tx;
    private final Consumer<UUID> callback;

    public PlayerFetcher(Main plugin, String playerName, Consumer<UUID> callback) {
        this.playerName = playerName;
        this.tx = plugin.textSystem();
        this.plugin = plugin;
        this.callback = callback;
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
        return i + j + k + l + rest;
    }


    @Override
    public void run() {

        tx.debugMessage("PlayerFetcher thread has started");


        Scanner scanner = null;
        try {
            URL url = new URL(LINK + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            tx.debugMessage("Connecting to Mojang: " + LINK + playerName);

            if(connection.getResponseCode() == 200) {
                StringBuilder response = new StringBuilder();
                scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }

                JSONParser parse = new JSONParser();
                JSONObject data = (JSONObject) parse.parse(response.toString());

                UUID uuid = UUID.fromString(getFullUUID((String) data.get("id")));
                tx.debugMessage("Built UUID from player " + playerName + " as: " + uuid);


                callback.accept(uuid);
            } else {
                callback.accept(null);
            }

        } catch (IOException | ParseException e) {
            callback.accept(null);
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                tx.debugMessage("Closing Scanner");
                scanner.close();
            }
        }
    }
}
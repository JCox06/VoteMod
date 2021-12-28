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

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.co.jcox.votemod.Main;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Matcher;

public class TextSystem {

    private final Main plugin;
    private final ResourceBundle language;
    private static Logger logger;

    public TextSystem(Main p) {
        logger = p.getLogger();
        plugin = p;

        String languageCode = plugin.getConfig().getString("lang");
        Locale locale = null;

        if(languageCode == null || languageCode.equalsIgnoreCase("default")) {
            //Set the locale to the JVM
            locale = Locale.getDefault();
        } else {
            locale = new Locale(languageCode);
        }

        this.language = ResourceBundle.getBundle("language", locale);
    }

    private String getLanguageValue(String key) {
        if(! this.language.containsKey(key)) {
            return "ERROR!";
        }
        return this.language.getString(key);
    }

    //Adds the [VoteMod] prefix when sending messengers to players
    private String decorateMessage(String message) {
        return ChatColor.AQUA + "[VoteMod]" + ChatColor.RESET + " " + message;
    }

    //Populate message with correct args
    private String populateMessage(String key, String args[]) {

        String message = getLanguageValue(key);

        int expectedArgs = 0;
        do {
            if(message.contains("{" + expectedArgs + "}")) {
                message.replace("{" + expectedArgs + "}", args[expectedArgs]);
                expectedArgs++;
            }
        } while(! message.contains("{" + expectedArgs + "}"));

        return message;
    }

    public void sendMessage(Player player, String key, String ...additional) {
        player.sendMessage(decorateMessage(populateMessage(key, additional)));
    }

    public void broadcastMessage(String key, String ...additional) {
        Bukkit.broadcastMessage(decorateMessage(populateMessage(key, additional)));
    }

    public void broadcastMessage(String permission, String key, String ...additional) {
        Bukkit.broadcast(decorateMessage(populateMessage(key, additional)), permission);
    }

    public void logMessage(String key, String ...additionals) {
        logger.info(populateMessage(key, additionals));
    }
}

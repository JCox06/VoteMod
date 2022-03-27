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

package uk.co.jcox.votemod.votes;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.co.jcox.votemod.Main;
import uk.co.jcox.votemod.util.PlayerFetcher;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VotePardon extends BaseVote{

    public VotePardon(Player sourcePlayer, String targetPlayerName, Main plugin) {
        super(sourcePlayer, targetPlayerName, plugin,"pardon");
    }

    @Override
    protected void onAction(String playerName) {
        Bukkit.getBanList(BanList.Type.NAME).pardon(playerName);
        Bukkit.getBanList(BanList.Type.IP).pardon(playerName);


        Bukkit.getScheduler().runTaskAsynchronously(plugin, new PlayerFetcher(plugin, playerName,  (uuid) -> {
            Bukkit.getScheduler().runTask(plugin, task -> Bukkit.getOfflinePlayer(uuid).setWhitelisted(true));
        }));

    }
}

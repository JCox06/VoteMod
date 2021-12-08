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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import uk.co.jcox.votemod.Main;
import uk.co.jcox.votemod.util.Messenger;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseVote {
    protected Main plugin;
    private List<Player> voters;
    private String targetPlayerName;
    private int requiredPlayers;
    private boolean isOnlineVote;

    protected BaseVote(Player sourcePlayer, String targetPlayerName, Main plugin, boolean isOnlineVote) {
        this.targetPlayerName = targetPlayerName;
        this.voters = new ArrayList<>();
        //The player to initialize the vote, will always be at index 0 in the list
        this.voters.add(0, sourcePlayer);
        this.isOnlineVote = isOnlineVote;
        this.plugin = plugin;
        requiredPlayers();
    }

    public Player getSourcePlayer() {
        return this.voters.get(0);
    }

    public String getTargetPlayer() {
        return this.targetPlayerName;
    }

    public final void addVoter(Player votingPlayer) {
        this.voters.add(votingPlayer);
            if (voters.size() >= requiredPlayers) {
                removeVote();
                onAction(targetPlayerName);
            }
    }

    private void requiredPlayers() {
        double votePercentage = plugin.getConfig().getInt("needed-votes");
        double playersOnline = Bukkit.getServer().getOnlinePlayers().size();
        double votesRequired = (votePercentage / 100) * playersOnline;
        //todo - This is not working
        this.requiredPlayers = (int) votesRequired;
        Messenger.log("REQUIRED-PLAYERS: " + requiredPlayers);
    }

    private void removeVote() {
       plugin.getVoteManager().remove(targetPlayerName, true);
       System.gc();
    }

    public void validate() {
        setTimeout();
    }

    private void setTimeout() {

        int timeout = plugin.getConfig().getInt("timeout") * 20;
        Runnable task = () -> {
            plugin.getVoteManager().remove(targetPlayerName, false);
        };

        plugin.getServer().getScheduler().runTaskLater(plugin, task, timeout);
    }

    public final int getRequired() {
        return requiredPlayers;
    }

    public boolean getVotersContains(Player player) {
        return voters.contains(player);
    }

    public int getNumberOfVoters() {
        return voters.size();
    }


    protected abstract void onAction(String playerName);
}
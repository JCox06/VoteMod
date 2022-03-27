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
//        FITNESS FOR A PARTICULAR PURPOSE AND hNONINFRINGEMENT. IN NO EVENT SHALL THE
//        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//        SOFTWARE.

package uk.co.jcox.votemod.votes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.co.jcox.votemod.Main;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseVote {
    private final List<Player> voters;
    private final String targetPlayerName;
    private final String name;
    protected Main plugin;
    private int requiredPlayers;

    protected BaseVote(Player sourcePlayer, String targetPlayerName, Main plugin, String name) {
        this.targetPlayerName = targetPlayerName;
        this.voters = new ArrayList<>();
        this.voters.add(0, sourcePlayer);
        this.plugin = plugin;
        this.name = name;
        calculateRequired();
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
            onAction(targetPlayerName);
            plugin.getVoteManager().remove(targetPlayerName, true);
        }
    }

    public String getType() {
        return this.name;
    }

    public final int getRequired() {
        return requiredPlayers;
    }

    public boolean containsVoter(Player player) {
        return voters.contains(player);
    }

    public int getNumberOfVoters() {
        return voters.size();
    }

//    private void calculateRequired() {
//        double votePercentage = plugin.getConfig().getInt("needed-votes");
//        double playersOnline = Bukkit.getServer().getOnlinePlayers().size();
//        double votesRequired = (votePercentage / 100) * playersOnline;
//        plugin.textSystem().debugMessage("Required Votes to become successful is: " + Math.round(votesRequired));
//        this.requiredPlayers = (int) Math.round(votesRequired);
//    }

    private void calculateRequired() {
        String configRequired = plugin.getConfig().getString("needed-votes");
        int players = Bukkit.getServer().getOnlinePlayers().size();

        if(configRequired.contains("%")) {
            int decimalMultiplier = Integer.parseInt(configRequired.replace("%", ""));
            this.requiredPlayers = (decimalMultiplier / 100) * players;
        }
        else if(configRequired.contains("-")) {
            int subtractor = Integer.parseInt(configRequired.replace("-", ""));
            int playersRequired = players - subtractor;
            if(playersRequired < 0) {
                this.requiredPlayers = 0;
            } else {
                this.requiredPlayers = playersRequired;
            }
        } else {
            this.requiredPlayers = plugin.getConfig().getInt("needed-votes");
        }
    }

    protected abstract void onAction(String playerName);
}

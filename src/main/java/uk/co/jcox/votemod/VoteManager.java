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

package uk.co.jcox.votemod;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import uk.co.jcox.votemod.util.PlayerFetcher;
import uk.co.jcox.votemod.votes.BaseVote;

import java.util.*;
import java.util.concurrent.CompletableFuture;


public class VoteManager {
    private final Map<String, BaseVote> ongoing;
    private final List<String> resistance;
    private final Main plugin;

    public VoteManager(Main plugin) {
        this.ongoing = new HashMap<>();
        this.resistance = new ArrayList<>();
        this.plugin = plugin;
    }

    private boolean checkVoter(Player source) {
        //Check to see if the voter has the permission votemod.vote
        if(Main.getPermissions().playerHas(source, "votemod.vote")) {
            return true;
        }
        //message that the player they do not have votemod.vote
        plugin.textSystem().sendMessage(source, "votemod-vote-message");

        return false;
    }


    private boolean checkEnvironmentConditions(Player source) {
        //Check if there are too many ongoing votes as well as check if are enough players online
        //check players online
        int online = Bukkit.getOnlinePlayers().size();
        int neededOnline = plugin.getConfig().getInt("required-players");
        if(online > neededOnline && online != 0) {
            plugin.textSystem().sendMessage(source, "less-required-message");
            return false;
        }

        //Check ongoing votes
        int numberOfVotes = ongoing.size();
        int max = plugin.getConfig().getInt("allowed-ongoing-votes");

        if(numberOfVotes >= max) {
            plugin.textSystem().sendMessage(source, "ongoing-message");
            return false;
        }
        return true;
    }


    public void newVote(BaseVote vote) {

        Player source = vote.getSourcePlayer();
        String target = vote.getTargetPlayer();

        if(! (checkEnvironmentConditions(source) && checkVoter(source))) {
            return;
        }

        if(resistance.contains(target)) {
            plugin.textSystem().sendMessage(source, "bypass-message");
            plugin.textSystem().debugMessage(source.getName() + " will not be voted as they have recently " +
                    "had a vote expire towards them");
            return;
        }

        //Check if the target has the bypass permission
        CompletableFuture<UUID> cf = new CompletableFuture<>();
        PlayerFetcher pf = new PlayerFetcher(target, cf, plugin.textSystem());
        Bukkit.getScheduler().runTaskAsynchronously(plugin, pf);
        plugin.textSystem().debugMessage("Waiting for response...");
        cf.whenComplete( (res, err) -> {
            plugin.textSystem().debugMessage("Sending vault request");
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(res);
            String world = Bukkit.getWorlds().get(0).getName();
            boolean hasBypass = Main.getPermissions().playerHas(world, offlinePlayer, "votemod.bypass");
            if(hasBypass) {
                plugin.textSystem().sendMessage(source, "bypass-message");
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.textSystem().debugMessage("Running task on main server thread");
                this.ongoing.put(target, vote);
                String type = plugin.textSystem().getResource(vote.getType());
                plugin.textSystem().broadcastMessage("started-vote-message", source.getName(), type,  vote.getTargetPlayer());
                setTimeout(target);
                plugin.textSystem().debugMessage("Added vote");
            });

        });
    }

    private boolean checkVoteExists(Player voter, String vote) {
        if(ongoing.containsKey(vote)) {
            return true;
        }

        plugin.textSystem().sendMessage(voter, "no-such-vote-message");
        return false;
    }

    private boolean checkVoteExists(String vote) {
        return ongoing.containsKey(vote);
    }


    public void vote(Player voter, String target) {
        if(! checkVoteExists(voter, target)) {
            return;
        }

        //Check if the voter has already voted on this particular type of vote
        if(ongoing.get(target).containsVoter(voter)) {
            //Voter has already voted
            plugin.textSystem().sendMessage(voter, "already-voted-message");
            return;
        }

        plugin.textSystem().broadcastMessage("pass-vote-message", voter.getName(), target);
        ongoing.get(target).addVoter(voter);
    }

    public boolean remove(Player player, String vote) {
        if( !checkVoteExists(player, vote)) {
            return false;
        }
        ongoing.remove(vote);
        addResistance(vote);
        plugin.textSystem().sendMessage(player, "removed-vote-message");
        return true;
    }

    public void remove(String vote, boolean successful) {
        ongoing.remove(vote);
        if(successful) {
            plugin.textSystem().broadcastMessage("successful-message", vote);

        } else {
            plugin.textSystem().broadcastMessage("expired-message", vote);
            addResistance(vote);
        }
    }

    public void addResistance(String vote) {
        //When a vote expires or is removed by an admin. The player will gain resistance
        if(plugin.getConfig().getBoolean("vote-resistance")) {
            plugin.textSystem().debugMessage("Added resistance for: " + vote);
            this.resistance.add(vote);

            Runnable task = () -> {
                resistance.remove(vote);
                plugin.textSystem().debugMessage(vote + " has been removed from the resistance list");
            };

            int time = 20 * 60 * plugin.getConfig().getInt("vote-resistance-expire");
            Bukkit.getScheduler().runTaskLater(plugin, task, time);
        }
    }


    private void setTimeout(String vote) {
        plugin.textSystem().debugMessage("Setting timeout for vote " + vote);
        int expire = plugin.getConfig().getInt("timeout") * 20;
        Runnable task = () -> {
            if(checkVoteExists(vote)) {
                remove(vote, false);
                plugin.textSystem().debugMessage("Removing expired vote: " + vote);
            }
        };
        Bukkit.getScheduler().runTaskLater(plugin, task, expire);
    }



    public void listVotes(Player player) {

        if(ongoing.size() == 0) {
            plugin.textSystem().sendMessage(player, "no-ongoing-votes-message");
            return;
        }
        for(BaseVote vote : ongoing.values()) {
            String name =vote.getTargetPlayer();
            int votes = vote.getNumberOfVoters();
            String type = plugin.textSystem().getResource(vote.getType());
            plugin.textSystem().sendMessage(player, "votes-message", type, name, votes + "");
        }
    }
}
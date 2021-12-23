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
import uk.co.jcox.votemod.util.Messenger;
import uk.co.jcox.votemod.util.PlayerFetcher;
import uk.co.jcox.votemod.votes.BaseVote;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class VoteManager {
    private final Map<String, BaseVote> ongoing;
    private final Main plugin;

    public VoteManager(Main plugin) {
        this.ongoing = new HashMap<>();
        this.plugin = plugin;
    }

    private boolean checkVoter(Player source) {
        //Check to see if the voter has the permission votemod.vote
        if(Main.getPermissions().playerHas(source, "votemod.vote")) {
            return true;
        }
        Messenger.sendMessage(source, plugin.getLangValue("votemod-vote-message"));
        return false;
    }


    private boolean checkEnvironmentConditions(Player source) {
        //Check if there are too many ongoing votes as well as check if are enough players online
        //check players online
        int online = Bukkit.getOnlinePlayers().size();
        int neededOnline = plugin.getConfig().getInt("required-players");
        if(online < neededOnline) {
            Messenger.sendMessage(source, plugin.getLangValue("less-required-message"));
            return false;
        }

        //Check ongoing votes
        int numberOfVotes = ongoing.size();
        int max = plugin.getConfig().getInt("allowed-ongoing-votes");

        if(numberOfVotes >= max) {
            Messenger.sendMessage(source, "ongoing-message");
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

        //Check if the target has the bypass permission
        CompletableFuture<UUID> cf = new CompletableFuture<>();
        PlayerFetcher pf = new PlayerFetcher(target, cf);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, pf);
        cf.whenComplete( (res, err) -> {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(res);
            String world = Bukkit.getWorlds().get(0).getName();
            boolean hasBypass = Main.getPermissions().playerHas(world, offlinePlayer, "votemod.bypass");
            if(hasBypass) {
                Messenger.sendMessage(source, plugin.getLangValue("bypass-message"));
                return;
            }
            System.out.println("[1]We are on the: " + Thread.currentThread().getName());

            Bukkit.getScheduler().runTask(plugin, () -> {

                this.ongoing.put(target, vote);
                String msg = plugin.getLangValue("started-vote-message");
                System.out.println("[2]We are on the: " + Thread.currentThread().getName());
                Messenger.broadcast(source.getName() + " " + msg + " " + vote.getTargetPlayer());
                setTimeout(target);
            });

        });
    }

    private boolean checkVoteExists(Player voter, String vote) {
        if(ongoing.containsKey(vote)) {
            return true;
        }

        Messenger.sendMessage(voter, "no-such-vote-message");
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
            Messenger.sendMessage(voter, "already-voted-message");
            return;
        }

        String message = plugin.getLangValue("pass-vote-message");
        Messenger.broadcast(voter.getName() + " " + message + " " + target);
        ongoing.get(target).addVoter(voter);
    }

    public boolean remove(Player player, String vote) {
        if( !checkVoteExists(player, vote)) {
            return false;
        }
        ongoing.remove(vote);

        Messenger.sendMessage(player, "removed-vote-message");
        return true;
    }

    public void remove(String vote) {
        ongoing.remove(vote);
    }


    private void setTimeout(String vote) {
        int expire = plugin.getConfig().getInt("timeout") * 20;
        Runnable task = () -> {
            if(checkVoteExists(vote)) {
                ongoing.remove(vote);
            }
        };
        Bukkit.getScheduler().runTaskLater(plugin, task, expire);
    }

    public void listVotes(Player player) {

        if(ongoing.size() == 0) {
            Messenger.sendMessage(player, plugin.getLangValue("no-ongoing-votes-message"));
            return;
        }
        for(BaseVote vote : ongoing.values()) {
            String name =vote.getTargetPlayer();
            int votes = vote.getNumberOfVoters();
            String type = plugin.getLangValue(vote.getName());
            String msg = (type + " " + name + " [" + votes + " " + plugin.getLangValue("votes-message") + "]");
            Messenger.sendMessage(player, msg);
        }
    }
}
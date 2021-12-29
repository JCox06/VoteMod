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

import co.aikar.commands.PaperCommandManager;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import uk.co.jcox.votemod.commands.CMDNewVote;
import uk.co.jcox.votemod.commands.CMDVote;
import uk.co.jcox.votemod.commands.CMDVoteMod;
import uk.co.jcox.votemod.util.TextSystem;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;


public class Main extends JavaPlugin {

    private FileConfiguration config;
    private VoteManager voteManager;
    private ResourceBundle language;
    private TextSystem ts;

    private static Permission permissions = null;
    private static String VERSION;

    private final boolean unitTesting;
    public static final String HOME_PAGE = "https://github.com/JCox06/VoteMod/";


    public Main() {
        super();
        unitTesting = false;
    }

    protected Main(JavaPluginLoader loader, PluginDescriptionFile description, File data, File file) {
        super(loader, description, data, file);
        unitTesting = true;
    }

    @Override
    public void onEnable() {
        this.ts = new TextSystem(this);
        initializeConfig();
        if(!unitTesting) checkServerOnlineState();
        VERSION = this.getDescription().getVersion();
        this.voteManager = new VoteManager(this);
        if(!unitTesting) initializeBstats();
        if(!unitTesting) initializeVault();
        initializeCMD();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }


    private void initializeConfig() {
        this.saveDefaultConfig();
        config = this.getConfig();
    }

    private void initializeBstats() {
        //For anonymous data collection
        int pluginID = 12378;
        new Metrics(this, pluginID);

    }


    private void initializeVault() {
        if(getServer().getPluginManager().getPlugin("Vault") == null) {
            ts.logMessage("permission-setup-fail-log");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        permissions = rsp.getProvider();

    }

    public void checkServerOnlineState() {
        if(! getServer().getOnlineMode() ) {
        textSystem().logMessage("online-mode-log");
        getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void initializeCMD() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new CMDNewVote(this));
        manager.registerCommand(new CMDVote(this));
        manager.registerCommand(new CMDVoteMod(this));
    }

    public VoteManager getVoteManager() {
        return this.voteManager;
    }

    public void setNewVoteManager() {
        this.voteManager = new VoteManager(this);
    }

    public static Permission getPermissions() {
        return permissions;
    }

    public TextSystem textSystem() {
        return this.ts;
    }

}

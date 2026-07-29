package me.bedepay.lighthealth;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.bedepay.lighthealth.command.LightHealthCommand;
import me.bedepay.lighthealth.config.Messages;
import me.bedepay.lighthealth.config.PluginConfig;
import me.bedepay.lighthealth.display.DisplayService;
import me.bedepay.lighthealth.listener.DamageListener;
import me.bedepay.lighthealth.listener.LookAtService;
import me.bedepay.lighthealth.util.PlayerPrefs;
import me.bedepay.lighthealth.util.Schedulers;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class LightHealth extends JavaPlugin {

    private PluginConfig config;
    private Messages messages;
    private PlayerPrefs prefs;
    private DisplayService displays;
    private LookAtService lookAt;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.config = PluginConfig.load(this);
        this.messages = new Messages(this);
        this.prefs = new PlayerPrefs();
        this.displays = new DisplayService(this);
        this.lookAt = new LookAtService(this);

        getServer().getPluginManager().registerEvents(new DamageListener(this), this);

        final LightHealthCommand command = new LightHealthCommand(this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(
                        command.build(),
                        "LightHealth — mob HP feedback",
                        List.of("lh", "mhp")
                ));

        this.lookAt.start();

        getLogger().info("LightHealth enabled ("
                + (Schedulers.isFolia() ? "Folia" : "Paper")
                + ") · lang=" + messages.language()
                + " look-at=" + config.lookAt().enabled()
                + " hologram=" + config.hologram()
                + " numbers=" + config.damageNumbers()
                + " actionbar=" + config.actionbar()
                + " bossbar=" + config.bossbar());
    }

    @Override
    public void onDisable() {
        if (this.lookAt != null) {
            this.lookAt.stop();
        }
        if (this.displays != null) {
            this.displays.shutdown();
        }
    }

    public void reloadAll() {
        this.config = PluginConfig.load(this);
        this.messages.reload();
        if (this.displays != null) {
            this.displays.reloadFormat();
        }
        if (this.lookAt != null) {
            this.lookAt.start();
        }
    }

    public PluginConfig config() {
        return config;
    }

    public Messages messages() {
        return messages;
    }

    public PlayerPrefs prefs() {
        return prefs;
    }

    public DisplayService displays() {
        return displays;
    }
}

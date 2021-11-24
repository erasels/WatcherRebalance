package WatcherRebalance;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CardStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

@SpireInitializer
public class WatcherRebalance implements
        PostInitializeSubscriber,
        EditKeywordsSubscriber,
        EditStringsSubscriber
{
    public static final Logger logger = LogManager.getLogger(WatcherRebalance.class);
    private static SpireConfig modConfig = null;

    public static void initialize() {
        BaseMod.subscribe(new WatcherRebalance());

        try {
            Properties defaults = new Properties();
            defaults.put("SkipLogging", Boolean.toString(true));
            modConfig = new SpireConfig("WatcherRebalance", "Config", defaults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean shouldSL() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("SkipLogging");
    }

    @Override
    public void receivePostInitialize() {
        ModPanel settingsPanel = new ModPanel();
        ModLabeledToggleButton HHBtn = new ModLabeledToggleButton("Skip all non-essential logging", 350, 700, Settings.CREAM_COLOR, FontHelper.charDescFont, shouldSL(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("SkipLogging", button.enabled);
                        try {
                            modConfig.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        settingsPanel.addUIElement(HHBtn);

        BaseMod.registerModBadge(ImageMaster.loadImage("WatcherRebalanceResources/img/modBadge.png"), "Watcher Rebalance", "erasels", "TODO", settingsPanel);
    }

    @Override
    public void receiveEditKeywords() {
        BaseMod.addKeyword("Wrath", new String[]{"wrath"},"In this #yStance, you deal and receive #b50% increased attack damage.");
        BaseMod.addKeyword("Calm", new String[]{"calm"},"Increases #yBlock gained from cards by #b33%.");
        BaseMod.addKeyword("Divinity", new String[]{"divinity"},"Upon entering this stance, gain [W] [W] [W] and draw 2 cards. Switch back to your previous #yStance at the start of your next turn.");
        BaseMod.addKeyword("Stance", new String[]{"stance", "stances"},"You can only have one stance at a time. NL Whenever you switch between them, gain #b1 #yMantra.");

        GameDictionary.WRATH.DESCRIPTION = "In this #yStance, you deal and receive #b50% increased attack damage.";
        GameDictionary.CALM.DESCRIPTION = "Increases #yBlock gained from cards by #b33%.";
        GameDictionary.ENLIGHTENMENT.DESCRIPTION = "Upon entering this stance, gain [W] [W] [W] and draw 2 cards. Switch back to your previous #yStance at the start of your next turn.";
        GameDictionary.STANCE.DESCRIPTION = GameDictionary.STANCE.DESCRIPTION + " NL Whenever you switch between them, gain #b1 #yMantra.";
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class, assetPath("loc/eng/cards.json"));
    }

    public static String assetPath(String path) {
        return "WatcherRebalanceResources/" + path;
    }
}
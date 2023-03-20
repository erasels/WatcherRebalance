package WatcherRebalance;

import WatcherRebalance.cards.NewSanctity;
import WatcherRebalance.cards.NewWreathOfFlames;
import WatcherRebalance.util.UC;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.helpers.CardBorderGlowManager;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.StanceStrings;
import com.megacrit.cardcrawl.stances.NeutralStance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

@SpireInitializer
public class WatcherRebalance implements
        PostInitializeSubscriber,
        EditKeywordsSubscriber,
        EditStringsSubscriber,
        EditCardsSubscriber
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

        BaseMod.registerModBadge(ImageMaster.loadImage("WatcherRebalanceResources/img/modBadge.png"), "Watcher Rebalance", "erasels", "TODO", settingsPanel);

        //Add custom glows to Empty card series when in a stance
        CardBorderGlowManager.addGlowInfo(new CardBorderGlowManager.GlowInfo() {
            @Override
            public boolean test(AbstractCard c) {
                return !NeutralStance.STANCE_ID.equals(UC.p().stance.ID) && c.hasTag(AbstractCard.CardTags.EMPTY);
            }

            @Override
            public Color getColor(AbstractCard c) {
                return Color.GOLD.cpy();
            }

            @Override
            public String glowID() {
                return "WatcherRebalance:EmptyCardsCustomGlow";
            }
        });
    }

    @Override
    public void receiveEditKeywords() {
        if(getLangString().equals("zhs")) {
            BaseMod.addKeyword("愤怒", new String[]{"愤怒", "wrath"},"处于这一 姿态 时, 你造成和受到的伤害增加50%。");
            BaseMod.addKeyword("平静", new String[]{"平静", "calm"},"从牌中获得的 格挡 增加2点 NL 退出这一 姿态 时, 获得 [W] 。");
            BaseMod.addKeyword("神格", new String[]{"神格", "divinity"},"进入这一 姿态 时, 获得 [W] [W] [W] 。 以这种 姿态 结束回合时, 保存你的能量 并 保留 你的手牌。 NL 在下个回合开始时，切换回上个 姿态 。");
            BaseMod.addKeyword("姿态", new String[]{"姿态", "stance", "stances"},"你只能同时处于一种姿态。 NL 当你转换到 愤怒 或 平静 , 获得 1 点 真言 。");

            BaseMod.addKeyword("watcherrebalance:" ,"回响", new String[]{"回响", "scryed", "scry"},"当这张牌通过 预见 弃置时, 该效果触发。");

            BaseMod.addKeyword("watcherrebalance:" ,"重洗", new String[]{"重洗", "reshuffle"},"打出时, 将这张牌洗入你的抽牌堆。");
        } else {
            BaseMod.addKeyword("Wrath", new String[]{"wrath"},"In this #yStance, you deal and receive #b50% increased attack damage.");
            BaseMod.addKeyword("Calm", new String[]{"calm"},"Increase #yBlock gained from cards by #b2. NL Upon exiting this #yStance, gain [W] .");
            BaseMod.addKeyword("Divinity", new String[]{"divinity"},"Upon entering this stance, gain [W] [W] [W] . When ending your turn in this stance, conserve your energy and #yRetain your cards. NL Switch back to your previous #yStance at the start of your next turn.");
            BaseMod.addKeyword("Stance", new String[]{"stance", "stances"},"You can only have one stance at a time. NL Whenever you switch into #yWrath or #yCalm, gain #b1 #yMantra.");

            BaseMod.addKeyword("watcherrebalance:" ,"Scryed", new String[]{"scryed", "scry"},"The effect triggers when the card is discarded via #yScry.");

            BaseMod.addKeyword("watcherrebalance:" ,"Reshuffle", new String[]{"reshuffle"},"When played, shuffle this card into your draw pile.");
        }
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class, assetPath("loc/eng/cards.json"));
        BaseMod.loadCustomStringsFile(StanceStrings.class, assetPath("loc/eng/stances.json"));

        //Jank but I don't expect more localization
        if(getLangString().equals("zhs")) {
            BaseMod.loadCustomStringsFile(CardStrings.class, assetPath("loc/zhs/cards.json"));
            BaseMod.loadCustomStringsFile(StanceStrings.class, assetPath("loc/zhs/stances.json"));
        }
    }

    public static String assetPath(String path) {
        return "WatcherRebalanceResources/" + path;
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new NewSanctity());
        BaseMod.addCard(new NewWreathOfFlames());
    }

    private String defaultLoc() {
        return "eng";
    }
    private String getLangString()
    {
        return Settings.language.name().toLowerCase();
    }
}
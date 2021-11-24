package WatcherRebalance.patches;

import WatcherRebalance.power.DelayedDamagePower;
import WatcherRebalance.util.UC;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.watcher.StanceCheckAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.Blasphemy;
import com.megacrit.cardcrawl.cards.purple.Eruption;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.stances.CalmStance;

public class CardPatches {
    //Blasphemy
    @SpirePatch2(clz = Blasphemy.class, method = SpirePatch.CONSTRUCTOR)
    public static class BlasphemyAddMagic {
        private static final int BLS_DMG = 10;
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.magicNumber = __instance.baseMagicNumber = BLS_DMG;
        }
    }
    @SpirePatch2(clz = Blasphemy.class, method = "use")
    public static class BlasphemyChange {
        @SpireInsertPatch(rloc = 1)
        public static SpireReturn<?> patch(AbstractCard __instance) {
            UC.doPow(new DelayedDamagePower(UC.p(), __instance.magicNumber));
            return SpireReturn.Return();
        }
    }

    //Eruption
    @SpirePatch2(clz = Eruption.class, method = SpirePatch.CONSTRUCTOR)
    public static class EruptionBuffDamage {
        private static final int DMG_INC = 3;
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.baseDamage += DMG_INC;
        }
    }

    @SpirePatch2(clz = Eruption.class, method = "use")
    public static class EruptionChange {
        @SpirePrefixPatch
        public static void patch(AbstractCard __instance) {
            UC.atb(new StanceCheckAction(CalmStance.STANCE_ID, new GainEnergyAction(__instance.upgraded?2:1)));
        }
    }

    @SpirePatch2(clz = Eruption.class, method = "upgrade")
    public static class EruptionRemoveCostUpgrade {
        @SpireInsertPatch(rloc = 2)
        public static SpireReturn<?> patch(AbstractCard __instance) {
            __instance.rawDescription = ((CardStrings)ReflectionHacks.getPrivateStatic(Eruption.class, "cardStrings")).UPGRADE_DESCRIPTION;
            //ReflectionHacks.privateMethod(AbstractCard.class, "upgradeDamage", int.class).invoke(__instance, 3);
            __instance.initializeDescription();
            return SpireReturn.Return();
        }
    }
}

package WatcherRebalance.patches;

import WatcherRebalance.power.EnterDivNextTurnPower;
import WatcherRebalance.util.UC;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.CalmStance;
import com.megacrit.cardcrawl.stances.DivinityStance;
import com.megacrit.cardcrawl.stances.WrathStance;
import javassist.CtBehavior;

public class StancePatches {
    //Wrath
    @SpirePatch2(clz = WrathStance.class, method = "atDamageReceive")
    @SpirePatch2(clz = WrathStance.class, method = "atDamageGive")
    public static class ReduceWrathNumbers {
        private static final float MULTI = 1.5f;
        //Inserts before return of multiplied damage
        @SpireInsertPatch(rloc = 1)
        public static SpireReturn<Float> patch(float damage) {
            return SpireReturn.Return(damage * MULTI);
        }
    }

    @SpirePatch2(clz = WrathStance.class, method = "updateDescription")
    public static class WrathDescChange {
        @SpirePostfixPatch
        public static void patch(AbstractStance __instance) {
            __instance.description = "Your Attacks deal #b50% increased damage and you take #b50% increased damage from attacks.";
        }
    }

    //Calm
    @SpirePatch2(clz = CalmStance.class, method = "onExitStance")
    public static class RemoveEnergyGain {
        @SpirePrefixPatch
        public static SpireReturn<?> patch(CalmStance __instance) {
            __instance.stopIdleSfx();
            return SpireReturn.Return();
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "applyPowersToBlock")
    public static class BlockModificationStancePatch {
        public static float BLK_MULTI = 1.333333333f;
        @SpireInsertPatch(locator = Locator.class, localvars = {"tmp"})
        public static void patch(AbstractCard __instance, @ByRef float[] tmp) {
            if(UC.p().stance instanceof CalmStance) {
                tmp[0] = tmp[0] * BLK_MULTI;
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = CalmStance.class, method = "updateDescription")
    public static class CalmDescChange {
        @SpirePostfixPatch
        public static void patch(AbstractStance __instance) {
            __instance.description = "Increase #yBlock gained from cards by #b33%.";
        }
    }

    //Divinity
    @SpirePatch2(clz=DivinityStance.class, method=SpirePatch.CLASS)
    public static class StanceField {
        public static SpireField<String> prevStance = new SpireField<>(() -> "");
    }

    @SpirePatch2(clz = DivinityStance.class, method = "atDamageGive")
    public static class RemoveDamageInc {
        @SpirePrefixPatch
        public static SpireReturn<Float> patch(float damage) {
            return SpireReturn.Return(damage);
        }
    }

    @SpirePatch2(clz = DivinityStance.class, method = "onEnterStance")
    public static class AddCardDrawOnEnterDiv {
        private static final int DIV_DRAW = 2;
        @SpirePostfixPatch
        public static void patch() {
            UC.doDraw(DIV_DRAW);
        }
    }

    @SpirePatch2(clz = ChangeStanceAction.class, method = "update")
    public static class StanceChangeDivMechanics {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(String ___id) {
            //DivStacking
            if(___id.equals(DivinityStance.STANCE_ID) && UC.p().stance.ID.equals(DivinityStance.STANCE_ID)) {
                    UC.doPow(new EnterDivNextTurnPower(UC.p(), 1));
            }
        }

        @SpireInsertPatch(locator = Locator2.class)
        public static void patch(AbstractStance ___newStance, String ___id) {
            //Divinity save stance
            if(___id.equals(DivinityStance.STANCE_ID)) {
                StanceField.prevStance.set(___newStance, UC.p().stance.ID);
                ___newStance.updateDescription();
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(String.class, "equals");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = DivinityStance.class, method = "atStartOfTurn")
    public static class ResumeStanceAfterDiv {
        @SpirePrefixPatch
        public static SpireReturn<?> patch(DivinityStance __instance) {
            UC.atb(new ChangeStanceAction(StanceField.prevStance.get(__instance)));
            return SpireReturn.Return();
        }
    }

    @SpirePatch2(clz = DivinityStance.class, method = "updateDescription")
    public static class DivinityDescChange {
        @SpirePostfixPatch
        public static void patch(AbstractStance __instance) {
            String prevStance = FontHelper.colorString(StanceField.prevStance.get(__instance), "y");
            __instance.description = String.format("Upon entering this #yStance, gain [W] [W] [W] and draw #b2 cards. NL Switch back to your previous #yStance at the start of your next turn. ( %s )", prevStance);
        }
    }

    //Mantra gain
    @SpirePatch2(clz = ChangeStanceAction.class, method = "update")
    public static class GainMantraOnChange {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch() {
            UC.doPow(new MantraPower(UC.p(), 1));
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "onStanceChange");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }
}

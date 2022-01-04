package WatcherRebalance.patches;

import WatcherRebalance.util.UC;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.CalmStance;
import com.megacrit.cardcrawl.stances.DivinityStance;
import com.megacrit.cardcrawl.stances.WrathStance;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

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

    //Calm
    @SpirePatch2(clz = CalmStance.class, method = "onExitStance")
    public static class RemoveEnergyGain {
        @SpirePrefixPatch
        public static SpireReturn<?> patch(CalmStance __instance) {
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
            __instance.stopIdleSfx();
            return SpireReturn.Return();
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "applyPowersToBlock")
    public static class BlockModificationStancePatch {
        public static float BLK_MULTI = 1.33333333333f;
        public static int BLK_ADD = 2;

        @SpireInsertPatch(locator = Locator.class, localvars = {"tmp"})
        public static void patch(AbstractCard __instance, @ByRef float[] tmp) {
            if (UC.p().stance instanceof CalmStance) {
                tmp[0] = tmp[0] + BLK_ADD;
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

    //Divinity
    @SpirePatch2(clz = DivinityStance.class, method = SpirePatch.CLASS)
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

    /*@SpirePatch2(clz = DivinityStance.class, method = "onEnterStance")
    public static class AddCardDrawOnEnterDiv {
        private static final int DIV_DRAW = 2;
        @SpirePostfixPatch
        public static void patch() {
            UC.doDraw(DIV_DRAW);
        }
    }*/

    @SpirePatch2(clz = DiscardAtEndOfTurnAction.class, method = "update")
    public static class DivinityRetainCards {
        @SpireInsertPatch(locator = Locator.class, localvars = {"e"})
        public static void patch(AbstractCard e) {
            if(DivinityStance.STANCE_ID.equals(UC.p().stance.ID))
                e.retain = true;
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "retain");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = EnergyManager.class, method = "recharge")
    public static class DivinityConserveEnergy {
        @SpireInstrumentPatch
        public static ExprEditor GoIntoIceCreamLogic() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractPlayer.class.getName()) && m.getMethodName().equals("hasPower")) {

                        m.replace("{ " +
                                "$_ = $proceed($$) || " + DivinityConserveEnergy.class.getName() + ".CheckForDivinity();" +
                                "}");
                    }
                }
            };
        }

        public static boolean CheckForDivinity() {
            return DivinityStance.STANCE_ID.equals(UC.p().stance.ID);
        }
    }

    @SpirePatch2(clz = ChangeStanceAction.class, method = "update")
    public static class StanceChangeDivMechanics {
        /*@SpireInsertPatch(locator = Locator.class)
        public static void patch(String ___id) {
            //DivStacking
            if(___id.equals(DivinityStance.STANCE_ID) && UC.p().stance.ID.equals(DivinityStance.STANCE_ID)) {
                    UC.doPow(new EnterDivNextTurnPower(UC.p(), 1));
            }
        }*/

        @SpireInsertPatch(locator = Locator2.class)
        public static void patch(AbstractStance ___newStance, String ___id) {
            //Divinity save stance
            if (___id.equals(DivinityStance.STANCE_ID)) {
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

    //Mantra gain
    @SpirePatch2(clz = ChangeStanceAction.class, method = "update")
    public static class GainMantraOnChange {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch() {
            if ((UC.p().chosenClass == AbstractPlayer.PlayerClass.WATCHER || UC.p().hasRelic(PrismaticShard.ID))
                    && (WrathStance.STANCE_ID.equals(UC.p().stance.ID) || CalmStance.STANCE_ID.equals(UC.p().stance.ID))) {
                UC.doPow(new MantraPower(UC.p(), 1));
            }
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

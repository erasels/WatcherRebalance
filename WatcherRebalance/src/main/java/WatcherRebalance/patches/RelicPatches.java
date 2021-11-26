package WatcherRebalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.tempCards.Miracle;
import com.megacrit.cardcrawl.relics.HolyWater;
import com.megacrit.cardcrawl.relics.PureWater;
import com.megacrit.cardcrawl.relics.VioletLotus;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

import static WatcherRebalance.patches.RelicPatches.ChangePureWaterDesc.MIRACLE_NAME;

public class RelicPatches {
    //Pure Water
    @SpirePatch2(clz = PureWater.class, method = "atBattleStartPreDraw")
    public static class BufferWaterMiracle {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(NewExpr ne) throws CannotCompileException {
                    if (ne.getClassName().equals(Miracle.class.getName())) {
                        ne.replace("{ " +
                                "$_ = $proceed($$);" +
                                "$_.upgrade();" +
                                "}");
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = PureWater.class, method = "getUpdatedDescription")
    public static class ChangePureWaterDesc {
        public static String MIRACLE_NAME;

        @SpirePostfixPatch
        public static String patch(String __result, PureWater __instance) {
            if (MIRACLE_NAME == null) {
                MIRACLE_NAME = new Miracle().name;
            }
            return __result.replace(MIRACLE_NAME, MIRACLE_NAME + "+");
        }
    }

    //Holy Water
    @SpirePatch2(clz = HolyWater.class, method = "atBattleStartPreDraw")
    public static class BufferHolyWaterMiracle {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(NewExpr ne) throws CannotCompileException {
                    if (ne.getClassName().equals(MakeTempCardInHandAction.class.getName())) {
                        ne.replace("{ " +
                                "$1.upgrade();" +
                                "$2 = 2;" +
                                "$_ = $proceed($$);" +
                                "}");
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = HolyWater.class, method = "getUpdatedDescription")
    public static class ChangeHolyWaterDesc {
        @SpirePostfixPatch
        public static String patch(String __result, HolyWater __instance) {
            if (MIRACLE_NAME == null) {
                MIRACLE_NAME = new Miracle().name;
            }
            __result = __result.replace(Integer.toString(3), Integer.toString(2));
            return __result.replace(MIRACLE_NAME, MIRACLE_NAME + "+");
        }
    }

    //Violet Lotus
    @SpirePatch2(clz = VioletLotus.class, method = "onChangeStance")
    public static class BuffLotus {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(NewExpr ne) throws CannotCompileException {
                    if (ne.getClassName().equals(GainEnergyAction.class.getName())) {
                        ne.replace("{ " +
                                "$1 = 2;" +
                                "$_ = $proceed($$);" +
                                "}");
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = VioletLotus.class, method = "getUpdatedDescription")
    public static class ChangeVioletLotusDesc {
        @SpirePostfixPatch
        public static String patch(String __result) {
            return "Whenever you exit #yCalm, gain [E] [E] .";
        }
    }
}

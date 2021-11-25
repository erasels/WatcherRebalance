package WatcherRebalance.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.GainPowerEffect;
import javassist.*;

import java.util.ArrayList;

public class PowerPatches {
    public static final float FLASH_TIMER = 1f;

    @SpirePatch(clz = MantraPower.class, method=SpirePatch.CLASS)
    public static class TimerField {
        public static SpireField<Float> timer = new SpireField<>(() -> FLASH_TIMER);
    }

    @SpirePatch(clz = MantraPower.class, method = SpirePatch.CONSTRUCTOR)
    public static class ChangeMethods {
        @SpireRawPatch
        public static void Raw(CtBehavior ctMethodToPatch) throws CannotCompileException {
            CtClass ctClass = ctMethodToPatch.getDeclaringClass();

            CtMethod method2 = CtNewMethod.make(
                    CtClass.voidType, // Return
                    "update", // Method name
                    new CtClass[]{CtPrimitiveType.intType}, //Paramters
                    null, // Exceptions
                    "{" +
                            "super.update($1);" +
                            ChangeMethods.class.getName()+".doFlashLogic(this);" +
                            "}",
                    ctClass
            );
            ctClass.addMethod(method2);
        }

        public static void doFlashLogic(MantraPower p) {
            if (p.amount == ((int)ReflectionHacks.getPrivate(p, MantraPower.class, "PRAYER_REQUIRED")) - 1){
                float t = TimerField.timer.get(p);
                if (t <= 0f){
                    ArrayList<AbstractGameEffect> effects = ReflectionHacks.getPrivateInherited(p, MantraPower.class, "effect");
                    effects.add(new GainPowerEffect(p));
                    TimerField.timer.set(p, FLASH_TIMER);
                } else {
                    TimerField.timer.set(p, t - Gdx.graphics.getRawDeltaTime());
                }
            }
        }
    }

    @SpirePatch2(clz = MantraPower.class, method = "playApplyPowerSfx")
    public static class DontSpamSfx {
        @SpirePrefixPatch
        public static SpireReturn<?> patch(MantraPower __instance) {
            if(__instance.amount == ((int)ReflectionHacks.getPrivate(__instance, MantraPower.class, "PRAYER_REQUIRED")) - 1) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}


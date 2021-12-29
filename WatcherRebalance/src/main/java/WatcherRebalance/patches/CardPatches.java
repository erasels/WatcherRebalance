package WatcherRebalance.patches;

import WatcherRebalance.cards.interfaces.WhenScriedCard;
import WatcherRebalance.power.DelayedDamagePower;
import WatcherRebalance.power.LikeWaterDrawPower;
import WatcherRebalance.power.LikeWaterEnergyPower;
import WatcherRebalance.util.UC;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.actions.watcher.FollowUpAction;
import com.megacrit.cardcrawl.actions.watcher.NotStanceCheckAction;
import com.megacrit.cardcrawl.actions.watcher.StanceCheckAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.cards.tempCards.ThroughViolence;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.stances.CalmStance;
import com.megacrit.cardcrawl.stances.DivinityStance;
import com.megacrit.cardcrawl.stances.NeutralStance;
import com.megacrit.cardcrawl.stances.WrathStance;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

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
        private static final int DMG_INC = 2;
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.cost = __instance.costForTurn = 1;
            __instance.baseDamage -= DMG_INC;
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
            __instance.initializeDescription();
            return SpireReturn.Return();
        }
    }

    @SpirePatch2(clz = Eruption.class, method = SpirePatch.CONSTRUCTOR)
    public static class EruptionGlow {
        @SpireRawPatch
        public static void Raw(CtBehavior ctMethodToPatch) throws CannotCompileException {
            CtClass ctClass = ctMethodToPatch.getDeclaringClass();

            CtMethod method2 = CtNewMethod.make(
                    CtClass.voidType, // Return
                    "triggerOnGlowCheck", // Method name
                    new CtClass[]{}, //Paramters
                    null, // Exceptions
                    "{" +
                            EruptionGlow.class.getName() + ".Do(this);" +
                            "}",
                    ctClass
            );
            ctClass.addMethod(method2);
        }

        public static void Do(AbstractCard c) {
            if (CalmStance.STANCE_ID.equals(UC.p().stance.ID)) {
                c.glowColor = Color.GOLD.cpy();
            } else {
                c.glowColor = new Color(0.2F, 0.9F, 1.0F, 0.25F);
            }
        }
    }

    //Signature Move
    @SpirePatch2(clz = SignatureMove.class, method = "canUse")
    public static class SMAddAdditionalUse {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Boolean> patch() {
            if(DivinityStance.STANCE_ID.equals(UC.p().stance.ID)) {
                return SpireReturn.Return(true);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(SignatureMove.class, "cantUseMessage");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = SignatureMove.class, method = "triggerOnGlowCheck")
    public static class SMFixGlow {
        @SpireInsertPatch(rloc = 9, localvars = {"glow"})
        public static void patch(@ByRef boolean[] glow) {
            if(!glow[0] && DivinityStance.STANCE_ID.equals(UC.p().stance.ID)) {
                glow[0] = true;
            }
        }
    }

    //Flying Sleeves
    @SpirePatch2(clz = FlyingSleeves.class, method = SpirePatch.CONSTRUCTOR)
    public static class FlyingSleevesIncDamage {
        private static final int INC_DMG = 1;
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.baseDamage += INC_DMG;
        }
    }

    //Master Reality
    @SpirePatch2(clz = MasterReality.class, method = "upgrade")
    public static class MRRemoveCostUpgrade {
        @SpireInsertPatch(rloc = 2)
        public static SpireReturn<?> patch(AbstractCard __instance) {
            __instance.rawDescription = ((CardStrings)ReflectionHacks.getPrivateStatic(MasterReality.class, "cardStrings")).UPGRADE_DESCRIPTION;
            __instance.initializeDescription();
            __instance.isInnate = true;
            return SpireReturn.Return();
        }
    }

    //Swivel
    @SpirePatch2(clz = Swivel.class, method = SpirePatch.CONSTRUCTOR)
    public static class SwivelIncBlk {
        private static final int INC_BLK = 1;
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.baseBlock += INC_BLK;
        }
    }

    //Like Water
    @SpirePatch2(clz = LikeWater.class, method = SpirePatch.CONSTRUCTOR)
    public static class LikeWaterChangeMagic {
        private static final int NEW_MAGIC = 1;
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.baseMagicNumber = __instance.magicNumber = NEW_MAGIC;
        }
    }

    @SpirePatch2(clz = LikeWater.class, method = "upgrade")
    public static class LikeWaterChangeUpgrade {
        @SpireInsertPatch(rloc = 2)
        public static SpireReturn<?> patch(AbstractCard __instance) {
            __instance.rawDescription = ((CardStrings)ReflectionHacks.getPrivateStatic(LikeWater.class, "cardStrings")).UPGRADE_DESCRIPTION;
            __instance.initializeDescription();
            return SpireReturn.Return();
        }
    }

    @SpirePatch2(clz = LikeWater.class, method = "use")
    public static class LikeWaterChange {
        @SpirePrefixPatch
        public static SpireReturn<?> patch(AbstractCard __instance) {
            if(!__instance.upgraded) {
                UC.doPow(new LikeWaterDrawPower(__instance.magicNumber));
            } else {
                UC.doPow(new LikeWaterEnergyPower(__instance.magicNumber));
            }
            return SpireReturn.Return();
        }
    }

    //Weave
    @SpirePatch2(clz = Weave.class, method = SpirePatch.CONSTRUCTOR)
    public static class WeaveChangeBaseValues {
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.cost = __instance.costForTurn = 1;
            __instance.baseDamage = 6;
            __instance.shuffleBackIntoDrawPile = true;
            __instance.magicNumber = __instance.baseMagicNumber = 13;
        }
    }

    @SpirePatch2(clz = Weave.class, method = "upgrade")
    public static class WeaveChangeUpgrade {
        @SpireInsertPatch(rloc = 2)
        public static void patch(AbstractCard __instance) {
            ReflectionHacks.privateMethod(AbstractCard.class, "upgradeMagicNumber", int.class).invoke(__instance, 2);
        }
    }

    @SpirePatch2(clz = Weave.class, method = "triggerOnScry")
    public static class RemovePreviousWeaveEffect {
        @SpirePrefixPatch
        public static SpireReturn<?> patch() {
            return SpireReturn.Return();
        }
    }

    @SpirePatch(clz = Weave.class, method = SpirePatch.CONSTRUCTOR)
    public static class AddWeaveScryedHook {
        @SpireRawPatch
        public static void patch(CtBehavior ctMethodToPatch) throws NotFoundException, CannotCompileException {
            CtClass ctClass = ctMethodToPatch.getDeclaringClass();
            ClassPool pool = ctClass.getClassPool();

            CtClass ctWhenScriedInterface = pool.get(WhenScriedCard.class.getName());
            ctClass.addInterface(ctWhenScriedInterface);

            CtMethod method = CtNewMethod.make(
                    CtClass.voidType, // Return
                    "whenScried", // Method name
                    new CtClass[]{},
                    null, // Exceptions
                    AddWeaveScryedHook.class.getName() + ".Do(this);",
                    ctClass
            );
            ctClass.addMethod(method);
        }

        public static void Do(AbstractCard c) {
            UC.atb(new DamageAllEnemiesAction(UC.p(), DamageInfo.createDamageMatrix(c.magicNumber, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        }
    }

    //Followup
    @SpirePatch2(clz = FollowUp.class, method = SpirePatch.CONSTRUCTOR)
    public static class FollowupChangeBaseValues {
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.baseDamage = 5;
        }
    }

    @SpirePatch2(clz = FollowUp.class, method = "use")
    public static class ChangeFollowup {
        @SpirePostfixPatch
        public static void patch() {
            UC.atb(new FollowUpAction());
        }
    }

    //Reach Heaven Through Violence
    @SpirePatch2(clz = ReachHeaven.class, method = SpirePatch.CONSTRUCTOR)
    public static class ReachHeavenChangeBaseValues {
        private static final int DMG_INC = 6;
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.baseDamage += DMG_INC;
        }
    }

    @SpirePatch2(clz = ThroughViolence.class, method = SpirePatch.CONSTRUCTOR)
    public static class ThroughViolenceChangeBaseValues {
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.selfRetain = false;
        }
    }

    @SpirePatch2(clz = ThroughViolence.class, method = "use")
    public static class ThroughViolenceChange {
        @SpirePostfixPatch
        public static void patch() {
            UC.atb(new StanceCheckAction(WrathStance.STANCE_ID, new ChangeStanceAction(CalmStance.STANCE_ID)));
            UC.atb(new StanceCheckAction(WrathStance.STANCE_ID, new GainEnergyAction(2)));}
    }

    @SpirePatch2(clz = ThroughViolence.class, method = SpirePatch.CONSTRUCTOR)
    public static class ThroughViolenceGlow {
        @SpireRawPatch
        public static void patch(CtBehavior ctMethodToPatch) throws CannotCompileException {
            CtClass ctClass = ctMethodToPatch.getDeclaringClass();

            CtMethod method2 = CtNewMethod.make(
                    CtClass.voidType, // Return
                    "triggerOnGlowCheck", // Method name
                    new CtClass[]{}, //Paramters
                    null, // Exceptions
                    "{" +
                            EruptionGlow.class.getName() + ".Do(this);" +
                            "}",
                    ctClass
            );
            ctClass.addMethod(method2);
        }
    }

    //Rushdown
    @SpirePatch2(clz = Rushdown.class, method = SpirePatch.CONSTRUCTOR)
    public static class RushdownChangeBaseValues {
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.baseMagicNumber = __instance.magicNumber = 1;
            __instance.cost = __instance.costForTurn = 2;
        }
    }

    @SpirePatch2(clz = Rushdown.class, method = "upgrade")
    public static class RushdownChangeCostUpgrade {
        @SpireInsertPatch(rloc = 2)
        public static SpireReturn<?> patch(AbstractCard __instance) {
            ReflectionHacks.privateMethod(AbstractCard.class, "upgradeBaseCost", int.class).invoke(__instance, 1);
            return SpireReturn.Return();
        }
    }

    //Conclude
    @SpirePatch2(clz = Conclude.class, method = SpirePatch.CONSTRUCTOR)
    public static class ConcludeChangeBaseValues {
        private static final int DMG_INC = 2;
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.baseDamage += DMG_INC;
        }
    }

    //Empty Mind
    @SpirePatch2(clz = EmptyMind.class, method = "use")
    public static class ChangeEmptyMind {
        @SpireInsertPatch(rloc = 5)
        public static void patch() {
            UC.atb(new NotStanceCheckAction(NeutralStance.STANCE_ID, new GainEnergyAction(1)));
        }
    }

    //Empty Fist
    @SpirePatch2(clz = EmptyFist.class, method = SpirePatch.CONSTRUCTOR)
    public static class EmptyFistChangeBaseValues {
        private static final int MAGIC = 1;
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.baseMagicNumber = __instance.magicNumber = MAGIC;
        }
    }

    @SpirePatch2(clz = EmptyFist.class, method = "use")
    public static class ChangeEmptyFist {
        @SpireInsertPatch(rloc = 5)
        public static void patch(AbstractCard __instance, AbstractPlayer p) {
            UC.atb(new NotStanceCheckAction(NeutralStance.STANCE_ID, new ApplyPowerAction(p, p, new StrengthPower(p, __instance.magicNumber), __instance.magicNumber)));
        }
    }

    //Empty Body
    @SpirePatch2(clz = EmptyBody.class, method = SpirePatch.CONSTRUCTOR)
    public static class EmptyBodyChangeBaseValues {
        private static final int MAGIC = 1;
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.baseMagicNumber = __instance.magicNumber = MAGIC;
        }
    }

    @SpirePatch2(clz = EmptyBody.class, method = "use")
    public static class ChangeEmptyBody {
        @SpireInsertPatch(rloc = 5)
        public static void patch(AbstractCard __instance, AbstractPlayer p) {
            UC.atb(new NotStanceCheckAction(NeutralStance.STANCE_ID, new ApplyPowerAction(p, p, new PlatedArmorPower(p, __instance.magicNumber), __instance.magicNumber)));
        }
    }

    //Windmill Strike
    @SpirePatch2(clz = WindmillStrike.class, method = SpirePatch.CONSTRUCTOR)
    public static class WindmillStrikeChangeBaseValues {
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.cost = __instance.costForTurn = 1;
            __instance.baseDamage -= 1;
        }
    }

    //SashWhip, CrushJoints
    @SpirePatch2(clz = SashWhip.class, method = SpirePatch.CONSTRUCTOR)
    @SpirePatch2(clz = CrushJoints.class, method = SpirePatch.CONSTRUCTOR)
    public static class SashCrushChangeBaseValues {
        private static final int DMG_INC = 1;
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.baseDamage += DMG_INC;
        }
    }

    //Deus Ex Machina
    @SpirePatch2(clz = DeusExMachina.class, method = SpirePatch.CONSTRUCTOR)
    public static class DeusExChangeBaseValues {
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.baseMagicNumber = __instance.magicNumber = 1;
            __instance.cardsToPreview.upgrade();
        }
    }

    @SpirePatch2(clz = DeusExMachina.class, method = "triggerWhenDrawn")
    public static class FixDumbExMachina {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                public void edit(NewExpr ne) throws CannotCompileException {
                    if (ne.getClassName().equals(MakeTempCardInHandAction.class.getName())) {
                        ne.replace("{ " +
                                "$1 = cardsToPreview;" +
                                "$_ = $proceed($$);" +
                                "}");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = DeusExMachina.class, method = SpirePatch.CONSTRUCTOR)
    public static class AddDeusExScryedHook {
        @SpireRawPatch
        public static void patch(CtBehavior ctMethodToPatch) throws NotFoundException, CannotCompileException {
            CtClass ctClass = ctMethodToPatch.getDeclaringClass();
            ClassPool pool = ctClass.getClassPool();

            CtClass ctWhenScriedInterface = pool.get(WhenScriedCard.class.getName());
            ctClass.addInterface(ctWhenScriedInterface);

            CtMethod method = CtNewMethod.make(
                    CtClass.voidType, // Return
                    "whenScried", // Method name
                    new CtClass[]{},
                    null, // Exceptions
                    AddDeusExScryedHook.class.getName() + ".Do(this);",
                    ctClass
            );
            ctClass.addMethod(method);
        }

        public static void Do(AbstractCard c) {
            UC.atb(new MakeTempCardInHandAction(c.cardsToPreview, c.magicNumber));
        }
    }

    @SpirePatch2(clz = DeusExMachina.class, method = "upgrade")
    public static class DeusExChangeUpgrade {
        @SpireInsertPatch(rloc = 2)
        public static void patch(AbstractCard __instance) {
            __instance.rawDescription = ((CardStrings)ReflectionHacks.getPrivateStatic(DeusExMachina.class, "cardStrings")).UPGRADE_DESCRIPTION;
            __instance.initializeDescription();
        }
    }

    //Carve Reality
    @SpirePatch2(clz = CarveReality.class, method = SpirePatch.CONSTRUCTOR)
    public static class CarveRealityAddStrikeTag {
        @SpirePostfixPatch
        public static void patch(AbstractCard __instance) {
            __instance.tags.add(AbstractCard.CardTags.STRIKE);
        }
    }

    //Establishment
    @SpirePatch2(clz = Establishment.class, method = "upgrade")
    public static class EstablishmentChangeUpgrade {
        @SpireInsertPatch(rloc = 2)
        public static SpireReturn<?> patch(AbstractCard __instance) {
            ReflectionHacks.privateMethod(AbstractCard.class, "upgradeBaseCost", int.class).invoke(__instance, 0);
            return SpireReturn.Return();
        }
    }

    //Pray
    @SpirePatch2(clz = Pray.class, method = "use")
    public static class PrayUseChange {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                public void edit(NewExpr ne) throws CannotCompileException {
                    if (ne.getClassName().equals(MakeTempCardInDrawPileAction.class.getName())) {
                        ne.replace("{ " +
                                "if(upgraded) {" +
                                "$2 = 2;" +
                                "}" +
                                "$_ = $proceed($$);" +
                                "}");
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = Pray.class, method = "upgrade")
    public static class PrayChangeUpgrade {
        @SpireInsertPatch(rloc = 2)
        public static SpireReturn<?> patch(AbstractCard __instance) {
            __instance.rawDescription = ((CardStrings)ReflectionHacks.getPrivateStatic(Pray.class, "cardStrings")).UPGRADE_DESCRIPTION;
            __instance.initializeDescription();
            return SpireReturn.Return();
        }
    }
}

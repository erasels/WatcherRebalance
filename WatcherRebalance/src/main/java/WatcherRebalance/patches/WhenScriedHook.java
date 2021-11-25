package WatcherRebalance.patches;

import WatcherRebalance.cards.interfaces.WhenScriedCard;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import javassist.CtBehavior;

public class WhenScriedHook {
    @SpirePatch2(clz = ScryAction.class, method = "update")
    public static class ScryHook {
        @SpireInsertPatch(locator = Locator.class, localvars = {"c"})
        public static void patch(AbstractCard c) {
            if(c instanceof WhenScriedCard)
                ((WhenScriedCard) c).whenScried();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "moveToDiscardPile");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }
}

package WatcherRebalance.cards;

import WatcherRebalance.util.UC;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class NewWreathOfFlames  extends AbstractCard {
    public static final String ID = "WreathOfFlame";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("WreathOfFlame");

    public NewWreathOfFlames() {
        super(ID, cardStrings.NAME, "purple/skill/wreathe_of_flame", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, AbstractCard.CardColor.PURPLE, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.SELF);
        baseMagicNumber = magicNumber = 4;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        UC.atb(new ScryAction(magicNumber));
        UC.doDraw(2);
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(3);
        }
    }

    public AbstractCard makeCopy() {
        return new NewWreathOfFlames();
    }
}
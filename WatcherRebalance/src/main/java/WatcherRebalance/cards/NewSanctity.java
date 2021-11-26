package WatcherRebalance.cards;

import WatcherRebalance.util.UC;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Miracle;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;

public class NewSanctity extends AbstractCard {
    public static final String ID = "Sanctity";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Sanctity");

    public NewSanctity() {
        super(ID, cardStrings.NAME, "purple/skill/sanctity", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, AbstractCard.CardColor.PURPLE, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.SELF);
        baseBlock = 6;
        baseMagicNumber = 3;
        magicNumber = baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        UC.doDef(this);
        UC.atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPower p = UC.p().getPower(MantraPower.POWER_ID);
                if(p != null && p.amount >= magicNumber) {
                    UC.atb(new MakeTempCardInDrawPileAction(new Miracle(), p.amount/magicNumber, true, true));
                }
                isDone = true;
            }
        });
    }

    public void triggerOnGlowCheck() {
        AbstractPower p = UC.p().getPower(MantraPower.POWER_ID);
        if(p != null && p.amount >= magicNumber) {
            glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBlock(2);
            upgradeMagicNumber(-1);
        }
    }

    public AbstractCard makeCopy() {
        return new NewSanctity();
    }
}


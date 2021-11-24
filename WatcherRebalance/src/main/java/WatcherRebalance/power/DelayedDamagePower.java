package WatcherRebalance.power;

import WatcherRebalance.util.UC;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DelayedDamagePower extends AbstractPower {
    public DelayedDamagePower(AbstractCreature owner, int amount) {
        this.name = "Delayed Damage";
        this.ID = "watcherRebalance:DelayedDamage";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("anger");
    }

    public void updateDescription() {
        this.description = String.format("At the start of your next turn, take #b%s damage.", amount);
    }

    @Override
    public void atStartOfTurn() {
        flash();
        UC.doDmg(owner, amount, DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE);
        UC.removePower(this);
    }
}


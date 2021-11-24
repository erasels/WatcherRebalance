package WatcherRebalance.power;

import WatcherRebalance.util.UC;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.stances.DivinityStance;

public class EnterDivNextTurnPower extends AbstractPower {
    public EnterDivNextTurnPower(AbstractCreature owner, int amount) {
        this.name = "Delayed Divinity";
        this.ID = "watcherRebalance:DelayedDivinity";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("skillBurn");
    }

    public void updateDescription() {
        this.description = String.format("Enter #yDivinity at the start of your next %d turns.", amount);
    }

    @Override
    public void atStartOfTurnPostDraw() {
        flashWithoutSound();
        UC.atb(new ChangeStanceAction(DivinityStance.STANCE_ID));
        UC.generalPowerLogic(this);
    }
}


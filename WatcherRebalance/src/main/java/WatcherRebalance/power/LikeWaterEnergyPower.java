package WatcherRebalance.power;

import WatcherRebalance.util.UC;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.CalmStance;

public class LikeWaterEnergyPower extends AbstractPower {
    public static final String POWER_ID = "waterRebalance:LikeWaterEnergyPower";

    public LikeWaterEnergyPower(int amt) {
        this.name = "Surge Like Water";
        this.ID = POWER_ID;
        this.owner = UC.p();
        this.amount = amt;
        updateDescription();
        loadRegion("like_water");
    }

    public void updateDescription() {
        this.description = String.format("Whenever you exit #yCalm, gain #b%d [E] .", amount);
    }

    @Override
    public void onChangeStance(AbstractStance oldStance, AbstractStance newStance) {
        if(CalmStance.STANCE_ID.equals(oldStance.ID)) {
            flashWithoutSound();
            UC.atb(new GainEnergyAction(amount));
        }
    }
}

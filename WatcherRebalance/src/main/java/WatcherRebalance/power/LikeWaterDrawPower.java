package WatcherRebalance.power;

import WatcherRebalance.util.UC;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.CalmStance;

public class LikeWaterDrawPower extends AbstractPower {
    public static final String POWER_ID = "waterRebalance:LikeWaterDrawPower";

    public LikeWaterDrawPower(int amt) {
        this.name = "Flow Like Water";
        this.ID = POWER_ID;
        this.owner = UC.p();
        this.amount = amt;
        updateDescription();
        loadRegion("like_water");
    }

    public void updateDescription() {
        String s = amount== 1 ? "card": "cards";
        this.description = String.format("Whenever you exit #yCalm, draw #b%d %s.", amount, s);
    }

    @Override
    public void onChangeStance(AbstractStance oldStance, AbstractStance newStance) {
        if(CalmStance.STANCE_ID.equals(oldStance.ID)) {
            UC.doDraw(amount);
        }
    }
}

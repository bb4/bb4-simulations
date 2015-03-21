package com.barrybecker4.simulation.trading.model.tradingstrategy;

import com.barrybecker4.simulation.trading.options.ChangePolicy;

/**
 * @author Barry Becker
 */
public class PercentOfReserveStrategy implements ITradingStrategy {

    private ChangePolicy gainPolicy;
    private ChangePolicy lossPolicy;

    public PercentOfReserveStrategy(ChangePolicy gainPolicy, ChangePolicy lossPolicy) {
        this.gainPolicy = gainPolicy;
        this.lossPolicy = lossPolicy;
    }


}

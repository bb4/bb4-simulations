package com.barrybecker4.simulation.trading.model.tradingstrategy;

/**
 * Represents the investors current state.
 * i.e. how much is invested, how much is held in reserve, etc.
 *
 * @author Barry Becker
 */
public class MarketPosition {

    private double invested;
    private double reserve;
    private double sharesOwned;

    public MarketPosition(double invested, double reserve, double sharesOwned) {
        this.invested = invested;
        this.reserve = reserve;
        this.sharesOwned = sharesOwned;
    }

    public double getInvested() {
        return invested;
    }

    public double getReserve() {
        return reserve;
    }

    public double getSharesOwned() {
        return sharesOwned;
    }
}

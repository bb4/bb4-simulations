/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.model.tradingstrategy;

import com.barrybecker4.simulation.trading.options.ChangePolicy;

/**
 * When the market dips, we buy with a percentage of our reserve.
 * Conversely, when the market goes up by some threshold percent, we sell a percentage of what is invested.
 *
 * @author Barry Becker
 */
public class BuyPercentOfReserveStrategy extends AbstractTradingStrategy {

    private ChangePolicy gainPolicy;
    private ChangePolicy lossPolicy;


    public BuyPercentOfReserveStrategy(double startingTotal, double startingInvestmentPercent,
                                       ChangePolicy gainPolicy, ChangePolicy lossPolicy) {
        super(startingTotal, startingInvestmentPercent);
        this.gainPolicy = gainPolicy;
        this.lossPolicy = lossPolicy;
    }


    /**
     * if this new price triggers a transaction, then do it
     */
    @Override
    public MarketPosition updateInvestment(double stockPrice) {

        double pctChange = (stockPrice - priceAtLastTransaction) / priceAtLastTransaction;
        if (pctChange >= gainPolicy.getChangePercent()) {
            // sell, and take some profit. Assume we can sell partial shares
            double sharesToSell = gainPolicy.getTransactPercent() * sharesOwned;
            //System.out.println(" - selling $" + (sharesToSell * stockPrice) + " which is " + sharesToSell + " shares @" + stockPrice);
            sharesOwned -= sharesToSell;
            reserve += sharesToSell * stockPrice;
            invested = sharesOwned * stockPrice;
            priceAtLastTransaction = stockPrice;
        }
        else if (-pctChange >= lossPolicy.getChangePercent()) {
            // buy more because its cheaper
            double amountToInvest = lossPolicy.getTransactPercent() * reserve;
            reserve -= amountToInvest;
            double sharesToBuy = amountToInvest / stockPrice;
            //System.out.println(" + buying $" + amountToInvest + " which is " + sharesToBuy + " shares @" + stockPrice);
            sharesOwned += sharesToBuy;
            invested = sharesOwned * stockPrice;
            priceAtLastTransaction = stockPrice;
        }
        return new MarketPosition(invested, reserve, sharesOwned);
    }

}

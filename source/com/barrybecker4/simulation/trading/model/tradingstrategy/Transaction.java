/*
 * Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/license.MIT
 */

package com.barrybecker4.simulation.trading.model.tradingstrategy;

/**
 * Contains the purchase price and how much was bought.
 * Immutable
 * @author Barry Becker
 */
public class Transaction {

    double stockPrice;
    double amount;
    double numShares;

    Transaction(double price, double amount) {
        this.stockPrice = price;
        this.amount = amount;
        this.numShares = amount / price;
    }

    public double getStockPrice() {
        return stockPrice;
    }

    public double getAmount() {
        return amount;
    }

    public double getNumShares() {
        return numShares;
    }

}

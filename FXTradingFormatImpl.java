package com.FXTrading.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;
import com.FXTrading.model.FXTradingEntity;

@Component
public class FXTradingFormatImpl implements FXTradingFormat {

    private List<FXTradingEntity> bookedTradesEntities = new ArrayList<>();

    @Override
    public Object doTrade(FXTradingEntity fxTradingEntity) {
        Map<String, String> errorsByUser = new TreeMap<>();

        if (fxTradingEntity.getCustomerName().isBlank()) {
            errorsByUser.put("Customer-Name", "Customer name cannot be empty");
        }
        if (fxTradingEntity.getUsd_Amount() < 0) {
            errorsByUser.put("USD-Amount", "Amount cannot be Negative");
        }
        if (!fxTradingEntity.getCurrencyPair().equals("USDINR")) {
            errorsByUser.put("Currency-Pair", "Currency pair is not USDINR");
        }

        if (errorsByUser.isEmpty()) {
            String indianAmount = converting_USD_To_INR(fxTradingEntity.getUsd_Amount());
            fxTradingEntity.setIndianAmount(indianAmount);
            this.bookedTradesEntities.add(fxTradingEntity);
            return fxTradingEntity;
        } else {
            return errorsByUser;
        }
    }

    @Override
    public List<FXTradingEntity> getTradeList() {
        return this.bookedTradesEntities;
    }

    private String converting_USD_To_INR(double amount) {
        double convertedAmount = amount * 66;

        DecimalFormat df = new DecimalFormat("#.##");
        String str = df.format(convertedAmount);

        // inserting ',' to get Indian format
        String formattedAmount = "";
        boolean setter = true;
        boolean thousandSetter = false;
        boolean otherSetter = false;
        int counter = 0;
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == '.') {
                formattedAmount = str.charAt(i) + formattedAmount;
                setter = false;
                thousandSetter = true;
                counter = 0;
            } else if (setter) {
                formattedAmount = str.charAt(i) + formattedAmount;

            } else if (thousandSetter && counter == 3) {
                formattedAmount = str.charAt(i) + "," + formattedAmount;
                thousandSetter = false;
                otherSetter = true;
                counter = 0;

            } else if (otherSetter && counter == 2) {
                formattedAmount = str.charAt(i) + "," + formattedAmount;
                counter = 0;
            } else {
                formattedAmount = str.charAt(i) + formattedAmount;
            }
            counter++;
        }
        return formattedAmount + " INR";
    }
}

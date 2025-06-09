package com.odms.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticsDeliveryResponse {
    private Integer orderCompleted;
    private Integer orderCancelled;
    private Integer orderPending;
    private Integer orderTotal;//

    private Double shippingFeeTotal;

    private Double commission;//
    private Integer ranking;
    private Double rankBonus;//
    private Double revenueBonus;//
    private Double earnings;//

    public StatisticsDeliveryResponse(
            Integer orderCompleted,
            Integer orderCancelled,
            Integer orderPending,
            Double shippingFeeTotal,
            Integer ranking
    ) {
        this.orderCompleted = orderCompleted;
        this.orderCancelled = orderCancelled;
        this.orderPending = orderPending;
        this.shippingFeeTotal = shippingFeeTotal;
        this.ranking = ranking;
    }


    public void setOrderTotal() {
        this.orderTotal = orderCompleted + orderCancelled + orderPending;
    }

    public void setCommission() {
        this.commission = this.shippingFeeTotal * 0.6;
    }

    public void setRankBonus() {
        if (this.ranking == 1) {
            this.rankBonus = 1000000.0;
        } else if (this.ranking == 2) {
            this.rankBonus = 700000.0;
        } else if (this.ranking == 3) {
            this.rankBonus = 500000.0;
        } else if (this.ranking >= 4 && this.ranking <= 10) {
            this.rankBonus = 300000.0;
        } else {
            this.rankBonus = 100000.0;
        }
    }

    public void setRevenueBonus() {
        if(this.shippingFeeTotal >= 15000000.0) {
            this.revenueBonus = 400000.0;
        } else if(this.shippingFeeTotal >= 1000000.0) {
            this.revenueBonus = 200000.0;
        } else {
            this.revenueBonus = 0.0;
        }
    }

    public void setEarnings() {
        this.earnings = this.commission + this.rankBonus + this.revenueBonus;
    }

}

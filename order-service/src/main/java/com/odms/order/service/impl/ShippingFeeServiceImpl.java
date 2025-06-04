package com.odms.order.service.impl;

import com.odms.order.dto.DistanceHeaderDTO;
import com.odms.order.dto.WeightRangeDTO;
import com.odms.order.dto.WeightRowDTO;
import com.odms.order.dto.response.ShippingMatrixResponse;
import com.odms.order.entity.DistanceRange;
import com.odms.order.entity.ShippingFee;
import com.odms.order.entity.WeightRange;
import com.odms.order.repository.DistanceRangeRepository;
import com.odms.order.repository.ShippingFeeRepository;
import com.odms.order.repository.WeightRangeRepository;
import com.odms.order.service.IShippingFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ShippingFeeServiceImpl implements IShippingFeeService {
    private final DistanceRangeRepository distanceRepo;
    private final WeightRangeRepository weightRepo;
    private final ShippingFeeRepository shippingRepo;

    @Override
    public ShippingMatrixResponse getShippingMatrix() {
        List<DistanceRange> distances = distanceRepo.findAll();
        List<WeightRange> weights = weightRepo.findAll();
        List<ShippingFee> fees = shippingRepo.findAll();

        distances.sort(Comparator.comparing(DistanceRange::getFromM));
        weights.sort(Comparator.comparing(WeightRange::getFromGam));

        // Map distance header
        List<DistanceHeaderDTO> distanceHeaders = distances.stream().map(d -> {
            String label = null;
            if(d.getToM() == null){
                label = ">" + this.normalizePrice(d.getFromM()) + "Km";
            } else {
                label = this.normalizePrice(d.getFromM()) + "-" + this.normalizePrice(d.getToM()) + "Km";
            }
            return DistanceHeaderDTO.builder()
                    .id(d.getId())
                    .label(label)
                    .build();
        }).toList();

        // Build rows
        List<WeightRowDTO> rows = new ArrayList<>();
        for (WeightRange weight : weights) {

            WeightRangeDTO weightDTO = WeightRangeDTO.builder()
                    .id(weight.getId())
                    .label(weight.getToGam() == null ?
                        ">" + this.normalizePrice(weight.getFromGam()) + "Kg" :
                            this.normalizePrice(weight.getFromGam()) + "-" + this.normalizePrice(weight.getToGam()) + "Kg")
                    .build();

            Map<Integer, String> priceMap = new HashMap<>();
            for (DistanceRange distance : distances) {
                fees.stream()
                        .filter(fee -> fee.getWeightRange().getId().equals(weight.getId()) &&
                                fee.getDistanceRange().getId().equals(distance.getId()))
                        .findFirst()
                        .ifPresent(fee -> priceMap.put(
                                distance.getId(), fee.getToPrice() == null ?
                                ">=" + this.normalizePrice(fee.getFromPrice()) :
                                this.normalizePrice(fee.getFromPrice()) + "-" + this.normalizePrice(fee.getToPrice())
                        ));
            }

            WeightRowDTO row = WeightRowDTO.builder()
                    .weightRange(weightDTO)
                    .prices(priceMap)
                    .build();
            rows.add(row);
        }

        return ShippingMatrixResponse.builder()
                .distanceHeaders(distanceHeaders)
                .rows(rows)
                .build();
    }

    @Override
    public Double calculateShippingFee(Double distance, Double weight) {
        distance = distance/1000; // Convert to km
        weight = weight/1000; // Convert to kg
        return 25000 + (distance * 2000) + (weight * 1000); // Base fee + distance fee + weight fee
    }

    private Number normalizePrice(Double value) {
        double result = value / 1000;
        if (Math.floor(result) == result) {
            return (int) result;
        } else {
            return result;
        }
    }
}

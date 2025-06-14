package com.odms.order.service.impl;

import com.odms.order.dto.DistanceHeaderDTO;
import com.odms.order.dto.WeightRangeDTO;
import com.odms.order.dto.WeightRowDTO;
import com.odms.order.dto.request.EstimateFeeRequest;
import com.odms.order.dto.response.EstimateFeeResponse;
import com.odms.order.dto.response.ShippingMatrixResponse;
import com.odms.order.entity.DistanceRange;
import com.odms.order.entity.ShippingFee;
import com.odms.order.entity.WeightRange;
import com.odms.order.exception.AppException;
import com.odms.order.exception.ErrorCode;
import com.odms.order.repository.DistanceRangeRepository;
import com.odms.order.repository.ShippingFeeRepository;
import com.odms.order.repository.WeightRangeRepository;
import com.odms.order.service.IGeoService;
import com.odms.order.service.IShippingFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ShippingFeeServiceImpl implements IShippingFeeService {
    private final DistanceRangeRepository distanceRepo;
    private final WeightRangeRepository weightRepo;
    private final ShippingFeeRepository shippingRepo;
    private final IGeoService geoService;
    private final DistanceRangeRepository distanceRangeRepository;

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
    public EstimateFeeResponse estimateShippingFee(EstimateFeeRequest request) {
        Double distance = geoService.getDistance(request.getPickupAddress(), request.getDeliveryAddress()); // in meters
        if (distance == null) {
            throw new AppException(ErrorCode.ERROR);
        }
        Double weight = request.getWeight() * 1000; // Convert kg to grams

        Double estimatedFee = this.calculateShippingFee(distance, weight);
        return EstimateFeeResponse.builder()
                .estimateFee(this.formatCurrency(estimatedFee))
                .estimateDistance(this.normalizePrice(distance) + " Km")
                .build();
    }

    @Override
    public Double calculateShippingFee(Double distance, Double weight) {
        DistanceRange distanceObject = distanceRangeRepository.findByDistanceRange(distance);
        WeightRange weightObject = weightRepo.findByWeightRange(weight);
        distance = distance/1000; // Convert to km
        weight = weight/1000; // Convert to kg
        return 25000 + (distance * distanceObject.getUnitPrice()) + (weight * weightObject.getUnitPrice()); // Base fee + distance fee + weight fee
    }

    private Number normalizePrice(Double value) {
        double result = value / 1000;
        if (Math.floor(result) == result) {
            return (int) result;
        } else {
            return Math.round(result * 100.0) / 100.0;
        }
    }

    private String formatCurrency(Double value) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(value) + " VND";
    }
}

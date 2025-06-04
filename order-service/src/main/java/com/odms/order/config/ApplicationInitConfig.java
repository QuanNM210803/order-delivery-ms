package com.odms.order.config;

import com.odms.order.entity.DistanceRange;
import com.odms.order.entity.ShippingFee;
import com.odms.order.entity.WeightRange;
import com.odms.order.repository.DistanceRangeRepository;
import com.odms.order.repository.ShippingFeeRepository;
import com.odms.order.repository.WeightRangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {
    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "org.postgresql.Driver")
    ApplicationRunner applicationRunner(DistanceRangeRepository distanceRangeRepository,
                                        WeightRangeRepository weightRangeRepository,
                                        ShippingFeeRepository shippingFeeRepository) {
        log.info("Initializing application.....");
        return args -> {
            if(distanceRangeRepository.count() > 0 || weightRangeRepository.count() > 0) {
                log.info("Distance and Weight ranges already initialized.");
                return;
            }
            DistanceRange distanceRange1 = DistanceRange.builder()
                    .fromM(0.)
                    .toM(4000.)
                    .build();
            DistanceRange distanceRange2 = DistanceRange.builder()
                    .fromM(4000.)
                    .toM(7000.)
                    .build();
            DistanceRange distanceRange3 = DistanceRange.builder()
                    .fromM(7000.)
                    .toM(10000.)
                    .build();
            DistanceRange distanceRange4 = DistanceRange.builder()
                    .fromM(10000.)
                    .build();

            WeightRange weightRange1 = WeightRange.builder()
                    .fromGam(0.)
                    .toGam(1000.)
                    .build();
            WeightRange weightRange2 = WeightRange.builder()
                    .fromGam(1000.)
                    .toGam(5000.)
                    .build();
            WeightRange weightRange3 = WeightRange.builder()
                    .fromGam(5000.)
                    .toGam(10000.)
                    .build();
            WeightRange weightRange4 = WeightRange.builder()
                    .fromGam(10000.)
                    .build();

            List<DistanceRange> distances = List.of(distanceRange1, distanceRange2, distanceRange3, distanceRange4);
            List<WeightRange> weights = List.of(weightRange1, weightRange2, weightRange3, weightRange4);
            distanceRangeRepository.saveAll(distances);
            weightRangeRepository.saveAll(weights);

            List<ShippingFee> shippingFees = new ArrayList<>();
            for (DistanceRange d : distances) {
                for (WeightRange w : weights) {
                    Double basePrice = 25000.;
                    Double distanceMin = Math.ceil((d.getFromM() != null ? d.getFromM() : 0) / 1000.0);
                    Double weightMin = Math.ceil((w.getFromGam() != null ? w.getFromGam() : 0) / 1000.0);
                    Double fromPrice = basePrice + distanceMin * 2000 + weightMin * 1000;

                    Double distanceMax = d.getToM() != null ? Math.ceil(d.getToM() / 1000.0) : null;
                    Double weightMax = w.getToGam() != null ? Math.ceil(w.getToGam() / 1000.0) : null;

                    Double toPrice = null;
                    if(distanceMax != null && weightMax != null) {
                        toPrice = basePrice + distanceMax * 2000 + weightMax * 1000;
                    }

                    ShippingFee priceEntry = ShippingFee.builder()
                            .distanceRange(d)
                            .weightRange(w)
                            .fromPrice(fromPrice)
                            .toPrice(toPrice)
                            .build();
                    shippingFees.add(priceEntry);
                }
            }
            shippingFeeRepository.saveAll(shippingFees);

            log.info("Application initialization completed .....");
        };
    }
}

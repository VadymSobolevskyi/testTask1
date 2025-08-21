package com.example.demo.service;

import com.example.demo.common.ComponentForProduceCycleDependency;
import com.example.demo.dto.CreateParcelRequest;
import com.example.demo.dto.ParcelSearchParams;
import com.example.demo.dto.ParcelStatistic;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.DeliveryType;
import com.example.demo.model.Parcel;
import com.example.demo.model.ParcelStatus;
import com.example.demo.repository.ParcelRepository;
import com.example.demo.service.parcel.price.DefaultDeliveryPriceCalculator;
import com.example.demo.service.parcel.price.EconomDeliveryPriceCalculator;
import com.example.demo.service.parcel.price.ExpressDeliveryPriceCalculator;
import com.example.demo.util.CommonGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParcelServiceImpl implements ParcelService {

    private final ParcelRepository parcelRepository;
    private final CommonGenerator generator;
    private final DefaultDeliveryPriceCalculator defaultDeliveryPriceCalculator;
    private final ExpressDeliveryPriceCalculator expressDeliveryPriceCalculator;
    private final EconomDeliveryPriceCalculator economDeliveryPriceCalculator;
    private final ComponentForProduceCycleDependency componentForProduceCycleDependency;

    @Override
    public Parcel getByTrackingNumber(String trackingNumber) {
        return parcelRepository.findByTrackingNumber(trackingNumber).orElseThrow(
                () -> new NotFoundException("Parcel with tracking number " + trackingNumber + " not found"));
    }

    @Override
    public Page<Parcel> findAll(ParcelSearchParams params, Pageable pageable) {
        // TODO implement search
        return new PageImpl<>(List.of());
    }

    @Override
    public ParcelStatistic buildStatistic(ParcelSearchParams params) {
        // TODO implement building of statistic
        return new ParcelStatistic();
    }

    @Override
    public Parcel create(CreateParcelRequest request) {
        Parcel parcel = new Parcel();
        parcel.setTrackingNumber(generator.uuid());
        parcel.setSender(request.getSender());
        parcel.setRecipient(request.getRecipient());
        parcel.setWeight(request.getWeight());
        parcel.setStatus(ParcelStatus.CREATED);
        parcel.setDeliveryType(request.getDeliveryType());

        if (request.getDeliveryType() == DeliveryType.ECONOM) {
            parcel.setPrice(economDeliveryPriceCalculator.calculatePrice(request));
        } else if (request.getDeliveryType() == DeliveryType.EXPRESS) {
            parcel.setPrice(expressDeliveryPriceCalculator.calculatePrice(request));
        } else if (request.getDeliveryType() == DeliveryType.DEFAULT) {
            parcel.setPrice(defaultDeliveryPriceCalculator.calculatePrice(request));
        } else {
            throw new NotFoundException();
        }

        return parcel;
    }

    @Override
    public Parcel updateStatus(String trackingNumber, ParcelStatus status) {
        Parcel parcel = getByTrackingNumber(trackingNumber);
        parcel.setStatus(status);
        save(parcel);
        return getByTrackingNumber(trackingNumber);
    }

    @Transactional
    public Parcel save(Parcel parcel) {
        return parcelRepository.save(parcel);
    }

    public void methodForTrick() {
        componentForProduceCycleDependency.doNothing();
    }
}

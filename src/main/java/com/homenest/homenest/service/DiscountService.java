package com.homenest.homenest.service;

import com.homenest.homenest.model.Discount;
import com.homenest.homenest.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountService {
    @Autowired
    private DiscountRepository discountRepository;

    public List<Discount> findAll() {
        return discountRepository.findAll();
    }

    public List<Discount> findActiveDiscounts() {
        return discountRepository.findByActiveTrue();
    }

    public Optional<Discount> findById(Long id) {
        return discountRepository.findById(id);
    }

    public Discount save(Discount discount) {
        return discountRepository.save(discount);
    }

    public void delete(Long id) {
        discountRepository.deleteById(id);
    }

}

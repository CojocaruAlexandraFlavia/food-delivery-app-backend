package com.example.fooddelivery.service;

import com.example.fooddelivery.model.*;
import com.example.fooddelivery.model.dto.HistoryDto;
import com.example.fooddelivery.repository.HistoryRepository;
import com.example.fooddelivery.repository.OrderRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final OrderRepository orderRepository;

    private ClientUserService clientUserService;
    private DeliveryUserService deliveryUserService;

    @Autowired
    public HistoryService(HistoryRepository historyRepository, OrderRepository orderRepository) {
        this.historyRepository = historyRepository;
        this.orderRepository = orderRepository;
    }

    public Optional<History> findHistoryById(Long id) {
        return historyRepository.findById(id);
    }


    public HistoryDto saveHistory(@NotNull HistoryDto historyDto){
        History history = new History();
        Optional<ClientUser> optionalClientUser = clientUserService.findClientUserById(historyDto.getClientUserId());
        Optional<DeliveryUser> optionalDeliveryUser = deliveryUserService.findDeliveryUserById(historyDto.getDeliveryUserId());

        if(optionalClientUser.isPresent() && optionalDeliveryUser.isPresent()) {
            //save history
            history.setClientUser(optionalClientUser.get());
            history.setDeliveryUser(optionalDeliveryUser.get());
            history = historyRepository.save(history);
            return HistoryDto.entityToDto(history);
        }
        return null;
    }

    public HistoryDto updateHistory(Long historyId, HistoryDto historyDto){
        Optional<History> optionalHistory = findHistoryById(historyId);
        if(optionalHistory.isPresent()){
            History history = optionalHistory.get();

            return HistoryDto.entityToDto(historyRepository.save(history));
        }
        return null;
    }

    public boolean deleteHistory(Long historyId){
        if(historyId != null){
            Optional<History> optionalHistory = findHistoryById(historyId);
            if(optionalHistory.isPresent()){
                historyRepository.delete(optionalHistory.get());
                return true;
            }
        }
        return false;
    }

    public HistoryDto addOrder(Long historyId, Long clientUserId, Long deliveryUserId, String number){
        Optional<History> optionalHistory = findHistoryById(historyId);
        Optional<ClientUser> optionalClientUser = clientUserService.findClientUserById(clientUserId);
        Optional<DeliveryUser> optionalDeliveryUser = deliveryUserService.findDeliveryUserById(deliveryUserId);

        if(optionalHistory.isPresent() && optionalClientUser.isPresent() && optionalDeliveryUser.isPresent()){
            History history = optionalHistory.get();
            Order o = new Order();
            o.setNumber(number);
            o.setHistory(history);
            orderRepository.save(o);
            return HistoryDto.entityToDto(historyRepository.save(history));
        }
        return null;
    }

}

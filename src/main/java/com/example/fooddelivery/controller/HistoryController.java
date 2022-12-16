package com.example.fooddelivery.controller;

import com.example.fooddelivery.model.History;
import com.example.fooddelivery.model.dto.AddOrderRequest;
import com.example.fooddelivery.model.dto.HistoryDto;
import com.example.fooddelivery.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Transactional
@RequestMapping("/history")
public class HistoryController {

    private HistoryService historyService;

    @Autowired
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping("/save")
    public ResponseEntity<HistoryDto> saveHistory(@RequestBody HistoryDto historyDto){
        return ResponseEntity.of(Optional.of(historyService.saveHistory(historyDto)));
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<HistoryDto> findHistoryById(@PathVariable("id") Long id){
        Optional<History> optionalHistory = historyService.findHistoryById(id);
        if(optionalHistory.isPresent()){
            HistoryDto historyDto = HistoryDto.entityToDto(optionalHistory.get());
            return new ResponseEntity<>(historyDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<?> deleteHistoryById(@PathVariable("id") Long id){
        boolean result = historyService.deleteHistory(id);
        if(result){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<HistoryDto> updateHistory(@PathVariable("id") Long id, @RequestBody HistoryDto dto){
        HistoryDto result = historyService.updateHistory(id, dto);
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/add-order")
    public ResponseEntity<HistoryDto> addOrder(@RequestBody AddOrderRequest addOrderRequest){
        HistoryDto result = historyService.addOrder(addOrderRequest.getHistoryId(), addOrderRequest.getClientUserId(),
                addOrderRequest.getDeliveryUserId(), addOrderRequest.getNumber());
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

}

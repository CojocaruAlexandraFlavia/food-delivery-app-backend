package com.example.fooddelivery.controller;

import com.example.fooddelivery.model.Location;
import com.example.fooddelivery.model.dto.LocationDto;
import com.example.fooddelivery.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.example.fooddelivery.model.dto.LocationDto.convertEntityToDto;

@RestController
@RequestMapping("/location")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/save")
    public ResponseEntity<LocationDto> saveLocation(@RequestBody LocationDto locationDto){
        return ResponseEntity.of(Optional.of(locationService.saveLocation(locationDto)));
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<LocationDto> findLocationById(@PathVariable("id") Long id){
        Optional<Location> optionalLocation = locationService.findLocationById(id);
        if(optionalLocation.isPresent()){
            LocationDto locationDto = convertEntityToDto(optionalLocation.get());
            return new ResponseEntity<>(locationDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<?> deleteLocationById(@PathVariable("id") Long id){
        boolean result = locationService.deleteLocation(id);
        if(result){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<LocationDto> updateLocation(@PathVariable("id") Long id, @RequestBody LocationDto dto){
        LocationDto result = locationService.updateLocation(id, dto);
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}

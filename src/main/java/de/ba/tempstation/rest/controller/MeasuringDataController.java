package de.ba.tempstation.rest.controller;

import de.ba.tempstation.client.WebSocketClient;
import de.ba.tempstation.db.model.MeasuringData;
import de.ba.tempstation.db.model.MeasuringStation;
import de.ba.tempstation.db.repository.EntityRepository;
import de.ba.tempstation.db.repository.MeasuringDataRepository;
import de.ba.tempstation.exception.NotFoundException;
import de.ba.tempstation.rest.dto.CreationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class MeasuringDataController {

    @Autowired
    private MeasuringDataRepository measuringDataRepository;

    @Autowired
    private EntityRepository<MeasuringStation> entityRepository;

    @Autowired
    private WebSocketClient webSocketClient;

    @PostMapping(value = "/measuringStations/{measuringStationId}/measuringData", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createMeasuringData(@RequestBody MeasuringData measuringData,
                                              @PathVariable("measuringStationId") int measuringStationId,
                                              UriComponentsBuilder builder) {

        MeasuringStation measuringStation = entityRepository.getEntityById(measuringStationId, MeasuringStation.class);
        if(measuringStation == null) {
            throw new NotFoundException();
        }

        measuringData.setMeasuringStationById(measuringStation.getId());
        measuringData.setLocationById(measuringStation.getLocation().getId());
        int id = measuringDataRepository.insertEntity(measuringData);

        webSocketClient.sendMeasuringData(id);

        URI uri = builder.path("api/measuringStations/{measuringStationId}/measuringData/{id}").buildAndExpand(measuringStationId, id).toUri();
        CreationResponseDTO creationResponse = new CreationResponseDTO(id, uri);
        return ResponseEntity.created(uri).body(creationResponse);
    }

    @GetMapping("/measuringStations/{measuringStationId}/measuringData/{measuringDataId}")
    public ResponseEntity getMeasuringDataByMeasuringStationId(@PathVariable("measuringStationId") int measuringStationId,
                                                               @PathVariable("measuringDataId") int measuringDataId) {
        MeasuringData measuringData = measuringDataRepository.getEntityById(measuringDataId, MeasuringData.class);
        if (measuringData == null || measuringData.getMeasuringStationId() != measuringStationId) {
            throw new NotFoundException();
        }
        return ResponseEntity.ok().body(measuringData);
    }

    @GetMapping("/measuringStations/{measuringStationId}/measuringData")
    public ResponseEntity getMeasuringDataByMeasuringStationId(@PathVariable("measuringStationId") int measuringStationId,
                                                               @RequestParam("unitId") int unitId,
                                                               @RequestParam(value = "from") String from,
                                                               @RequestParam(value = "to", required = false) String to) {
        Calendar toTime = Calendar.getInstance();
        Calendar fromTime = Calendar.getInstance();
        if (to != null) {
            toTime.setTimeInMillis(new Long(to));
        }
        fromTime.setTimeInMillis(new Long(from));
        List<MeasuringData> measuringData = measuringDataRepository.getMeasuringDataByMeasuringStationId(measuringStationId, unitId, fromTime, toTime);
        return ResponseEntity.ok().body(measuringData);
    }

    @GetMapping("/locations/{locationId}/measuringData/{measuringDataId}")
    public ResponseEntity getMeasuringDataByLocationId(@PathVariable("locationId") int locationId,
                                                       @PathVariable("measuringDataId") int measuringDataId) {
        MeasuringData measuringData = measuringDataRepository.getEntityById(measuringDataId, MeasuringData.class);
        if (measuringData == null || measuringData.getLocationId() != locationId) {
            throw new NotFoundException();
        }
        return ResponseEntity.ok().body(measuringData);
    }

    @GetMapping("/locations/{locationId}/measuringData")
    public ResponseEntity getMeasuringDataByLocationId(@PathVariable("locationId") int locationId,
                                                       @RequestParam("unitId") int unitId,
                                                       @RequestParam(value = "from") String from,
                                                       @RequestParam(value = "to", required = false) String to) {
        Calendar toTime = Calendar.getInstance();
        Calendar fromTime = Calendar.getInstance();
        if (to != null) {
            toTime.setTimeInMillis(new Long(to));
        }
        fromTime.setTimeInMillis(new Long(from));
        List<MeasuringData> measuringData = measuringDataRepository.getMeasuringDataByLocationId(locationId, unitId, fromTime, toTime);
        return ResponseEntity.ok().body(measuringData);
    }
}

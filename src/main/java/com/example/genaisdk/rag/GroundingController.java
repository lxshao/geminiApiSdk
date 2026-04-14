package com.example.genaisdk.rag;

import com.example.genaisdk.dto.GroundingRequest;
import com.example.genaisdk.dto.GroundingResponse;
import com.example.genaisdk.service.GroundingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class GroundingController {

    private final GroundingService groundingService;

    public GroundingController(GroundingService groundingService) {
        this.groundingService = groundingService;
    }

    @PostMapping("/api/v1/grounding")
    public GroundingResponse getGrounding(@RequestBody GroundingRequest groundingRequest) {
        return groundingService.grounding(groundingRequest);
    }

    @PostMapping("/api/v2/grounding")
    public GroundingResponse getGrounding2(@RequestBody GroundingRequest groundingRequest) {
        return groundingService.retrieveAnswer(groundingRequest);
    }

}

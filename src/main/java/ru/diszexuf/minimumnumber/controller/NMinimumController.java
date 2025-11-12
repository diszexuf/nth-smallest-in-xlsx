package ru.diszexuf.minimumnumber.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.diszexuf.minimumnumber.api.NthMinimumApi;
import ru.diszexuf.minimumnumber.model.NthMinimumRequest;
import ru.diszexuf.minimumnumber.service.NthMinimumService;

@RestController
@RequiredArgsConstructor
public class NMinimumController implements NthMinimumApi {

    private final NthMinimumService nthMinimumService;

    @Override
    public ResponseEntity<Integer> findNthMinimum(@Valid @RequestBody NthMinimumRequest request) {
        return ResponseEntity.ok(nthMinimumService.findNthMinimum(request));
    }
}

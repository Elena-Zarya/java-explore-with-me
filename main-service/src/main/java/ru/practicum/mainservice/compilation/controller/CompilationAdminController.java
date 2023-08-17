package ru.practicum.mainservice.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.dto.UpdateCompilationRequest;
import ru.practicum.mainservice.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@Slf4j
@RequiredArgsConstructor
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> addNewCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Received POST request: add new compilations {}", newCompilationDto);
        return new ResponseEntity<>(compilationService.addNewCompilation(newCompilationDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<CompilationDto> deleteCompilation(@PathVariable Long compId) {
        log.info("Received DELETE request: delete compilation id {}", compId);
        compilationService.deleteCompilation(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Long compId,
                                                            @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Received PATCH request: update compilation id {}", compId);
        return new ResponseEntity<>(compilationService.updateCompilation(compId, updateCompilationRequest), HttpStatus.OK);
    }
}

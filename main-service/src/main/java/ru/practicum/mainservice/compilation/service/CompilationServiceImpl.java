package ru.practicum.mainservice.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.Pages;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.dto.UpdateCompilationRequest;
import ru.practicum.mainservice.compilation.mapper.CompilationMapper;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.exception.IncorrectRequestException;
import ru.practicum.mainservice.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventService eventService;
    private final EventMapper eventMapper;

    @Transactional
    @Override
    public CompilationDto addNewCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(newCompilationDto);
        Compilation savedCompilation = compilationRepository.save(compilation);
        log.info("Add new compilation {}", savedCompilation);
        return compilationMapper.compilationToCompilationDto(savedCompilation);
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        CompilationDto compilationDto = getCompilationById(compId);
        compilationRepository.deleteById(compId);
        log.info("Delete compilation id = {}", compId);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        CompilationDto compilationDto = getCompilationById(compId);
        Compilation compilation = compilationMapper.compilationDtoToCompilation(compilationDto);
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            if (updateCompilationRequest.getTitle().length() > 50) {
                throw new IncorrectRequestException("Length title should be less than 50");
            }
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getEvents() != null) {
            List<Long> eventsId = updateCompilationRequest.getEvents();
            List<Event> events = new ArrayList<>();
            for (Long eventId : eventsId) {
                events.add(eventMapper.eventFullDtoToEvent(eventService.getEventFullById(eventId)));
            }
            compilation.setEvents(events);
        }
        Compilation updatedCompilation = compilationRepository.save(compilation);
        log.info("Update compilation id = {}", compId);
        return compilationMapper.compilationToCompilationDto(updatedCompilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest page = Pages.getPage(from, size);
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(page).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, page);
        }
        log.info("Get compilations {}", compilations);
        return compilations.stream()
                .map(compilationMapper::compilationToCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);
        if (compilation.isEmpty()) {
            throw new NotFoundException("compilation " + compId + " not found");
        }
        return compilationMapper.compilationToCompilationDto(compilation.get());
    }
}

package com.sociogram.main_service.compilation.service;

import com.sociogram.main_service.EventClient;
import com.sociogram.main_service.compilation.dto.CompilationDto;
import com.sociogram.main_service.compilation.dto.NewCompilationDto;
import com.sociogram.main_service.compilation.dto.UpdateCompilationRequest;
import com.sociogram.main_service.compilation.mapper.CompilationMapper;
import com.sociogram.main_service.compilation.model.Compilation;
import com.sociogram.main_service.compilation.repository.CompilationsRepository;
import com.sociogram.main_service.event.dto.EventShortDto;
import com.sociogram.main_service.event.model.Event;
import com.sociogram.main_service.event.repository.EventRepository;
import com.sociogram.main_service.exception.EntityNotFoundException;
import com.sociogram.main_service.exception.UserNotFoundException;
import com.sun.nio.sctp.IllegalReceiveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class  CompilationServiceImpl implements CompilationService {

    private final CompilationsRepository compilationsRepository;
    private final EventRepository eventRepository;
    private final EventClient eventClient;

    @Override
    @Transactional
    public CompilationDto createNewCompilation(NewCompilationDto newCompilationDto) {
        Set<Long> eventsIds = newCompilationDto.getEvents();
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        if (eventsIds != null) {
            List<Event> events = eventRepository.findAllById(eventsIds);
            compilation.setEvents(new HashSet<>(events));
        }
        Compilation savedCompilation = compilationsRepository.save(compilation);
        if (savedCompilation.getEvents() == null) {
            return CompilationMapper.toCompilationDto(savedCompilation, null);
        }
        List<EventShortDto> eventsDto = eventClient.makeEventShortDto(savedCompilation.getEvents());
        log.info("The selection has been saved.");
        return CompilationMapper.toCompilationDto(savedCompilation, eventsDto);
    }


    @Transactional
    @Override
    public void removeCompilation(Long id) {
        try {
            compilationsRepository.deleteById(id);
            log.info("The selection has been removed.");
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("Selection not found");
        }
    }

    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = getCompilation(compId);
        List<EventShortDto> eventsDto = eventClient.makeEventShortDto(compilation.getEvents());
        return CompilationMapper.toCompilationDto(compilation, eventsDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilationsByPinned(Boolean pinned, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalReceiveException("Invalid parameter specified");
        }
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationsRepository.findAllByPinned(pinned, PageRequest.of(from / size, size));
        } else {
            compilations = compilationsRepository.findAll(PageRequest.of(from / size, size)).getContent();
        }

        if (compilations.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Event> events = compilations.stream()
                .flatMap(compilation -> compilation.getEvents().stream())
                .collect(Collectors.toSet());
        List<EventShortDto> eventsDtoList = eventClient.makeEventShortDto(events);

        Map<Long, EventShortDto> eventDtosMap = new HashMap<>();
        for (EventShortDto eventShortDto : eventsDtoList) {
            eventDtosMap.put(
                    eventShortDto.getId(),
                    eventShortDto
            );
        }

        Map<Long, List<EventShortDto>> eventsDtoMapByCompilationId = compilations.stream()
                .collect(Collectors.toMap(Compilation::getId, compilation -> {
                    Set<Event> eventsSet = compilation.getEvents();
                    return eventsSet.stream()
                            .map(event -> eventDtosMap.get(event.getId()))
                            .collect(Collectors.toList());
                }));

        log.info("Received a list of all selections.");
        return compilations.stream()
                .map(compilation -> {
                    Long compilationId = compilation.getId();
                    List<EventShortDto> eventShortDtos = eventsDtoMapByCompilationId.get(compilationId);
                    return CompilationMapper.toCompilationDto(compilation, eventShortDtos);
                }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = getCompilation(compId);

        Set<Long> eventsIds = updateCompilationRequest.getEvents();
        if (eventsIds != null) {
            List<Event> events = eventRepository.findAllById(eventsIds);
            compilation.setEvents(new HashSet<>(events));
        }
        if (updateCompilationRequest.getEvents() != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(updateCompilationRequest.getEvents()));
            CompilationMapper.fromUpdateCompilationDtoToCompilation(updateCompilationRequest, compilation, events);
        }

        CompilationMapper.fromUpdateCompilationDtoToCompilation(updateCompilationRequest, compilation, null);

        List<EventShortDto> eventsDto = eventClient.makeEventShortDto(compilation.getEvents());

        Set<Event> updatedEvents = compilationsRepository.save(compilation).getEvents();

        return CompilationMapper.toCompilationDto(compilation, eventsDto);
    }

    private Compilation getCompilation(Long compId) {
        return compilationsRepository.findById(compId).orElseThrow(() ->
                new EntityNotFoundException("Invalid collection ID."));
    }
}

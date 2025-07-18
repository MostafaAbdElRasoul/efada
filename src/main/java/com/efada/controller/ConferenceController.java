package com.efada.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.efada.base.BaseController;
import com.efada.base.BaseResponse;
import com.efada.dto.AppUserDTO;
import com.efada.dto.ConferenceDTO;
import com.efada.entity.Conference;
import com.efada.serviceImpl.ConferenceServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/conferences")
@Tag(name = "Conferences", description = "Operations related to conferences")
public class ConferenceController extends BaseController<Long, ConferenceDTO, ConferenceServiceImpl>{

	@Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ConferenceDTO>> insert(@RequestBody @Valid ConferenceDTO dto) {
        BaseResponse<ConferenceDTO> response = BaseResponse.<ConferenceDTO>builder()
                .data(baseServiceImpl.save(dto))
                .code(HttpStatus.CREATED.value())
                .status(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

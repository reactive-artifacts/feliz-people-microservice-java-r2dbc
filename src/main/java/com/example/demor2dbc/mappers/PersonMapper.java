package com.example.demor2dbc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.demor2dbc.entities.read.Person;
import com.example.demor2dbc.web.dto.response.HrPersonDto;

@Mapper
public interface PersonMapper {
	PersonMapper INSTANCE = Mappers.getMapper( PersonMapper.class );
    HrPersonDto personToHrPersonDto(Person person);
}
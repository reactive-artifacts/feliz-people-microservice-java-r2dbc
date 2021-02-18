package com.example.demor2dbc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

import com.example.demor2dbc.entities.read.Job;
import com.example.demor2dbc.entities.read.Person;

@Mapper(mappingControl = DeepClone.class)
public interface JobCloneMapper {
	JobCloneMapper INSTANCE = Mappers.getMapper( JobCloneMapper.class );
    Job clone(Job job);
}
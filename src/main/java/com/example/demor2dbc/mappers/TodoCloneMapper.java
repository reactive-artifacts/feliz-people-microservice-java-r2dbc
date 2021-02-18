package com.example.demor2dbc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

import com.example.demor2dbc.entities.read.Todo;

@Mapper(mappingControl = DeepClone.class)
public interface TodoCloneMapper {
	TodoCloneMapper INSTANCE = Mappers.getMapper( TodoCloneMapper.class );
    Todo clone(Todo todo);
}
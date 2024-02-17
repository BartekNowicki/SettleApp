package com.application.settleApp.mappers;

public interface Mapper<Entity, EntityDTO> {
  EntityDTO toDTO(Entity entity);

  Entity fromDTO(EntityDTO entityDTO);
}

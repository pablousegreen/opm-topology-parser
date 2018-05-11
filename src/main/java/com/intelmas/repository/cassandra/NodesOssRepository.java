package com.intelmas.repository.cassandra;

import org.springframework.data.repository.CrudRepository;

import com.intelmas.dto.model.NodesEntityKey;
import com.intelmas.dto.model.NodesOssEntity;

public interface NodesOssRepository extends CrudRepository<NodesOssEntity, NodesEntityKey> {
	
}

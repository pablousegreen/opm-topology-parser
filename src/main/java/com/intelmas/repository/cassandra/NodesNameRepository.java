package com.intelmas.repository.cassandra;

import org.springframework.data.repository.CrudRepository;

import com.intelmas.dto.model.NodeTopologyEntity;

public interface NodesNameRepository extends CrudRepository<NodeTopologyEntity, String> {

}

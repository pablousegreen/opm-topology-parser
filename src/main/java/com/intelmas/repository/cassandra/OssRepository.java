package com.intelmas.repository.cassandra;

import java.util.UUID;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.intelmas.dto.model.OssEntity;

public interface OssRepository extends CrudRepository<OssEntity, UUID>{

	@Query("SELECT * FROM oss_list WHERE oss_organisation = :organisation AND oss_name = :name")
	OssEntity findByName(@Param("organisation") String organisation, @Param("name") String name);

}

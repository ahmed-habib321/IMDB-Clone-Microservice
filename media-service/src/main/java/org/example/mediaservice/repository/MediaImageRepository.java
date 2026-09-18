package org.example.mediaservice.repository;


import org.example.mediaservice.model.MediaImage;
import org.example.mediaservice.model.enums.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaImageRepository extends JpaRepository<MediaImage, UUID> {
    List<MediaImage> findByTitleId(UUID titleId);
    List<MediaImage> findByPersonId(UUID personId);
    List<MediaImage> findByTitleIdAndImageType(UUID titleId, ImageType imageType);
    List<MediaImage> findByPersonIdAndImageType(UUID personId, ImageType imageType);
}

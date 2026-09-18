package org.example.mediaservice.mapper;

import org.example.mediaservice.dto.boxOffice.BoxOfficeDTO;
import org.example.mediaservice.dto.image.ImageRequest;
import org.example.mediaservice.dto.image.ImageResponse;
import org.example.mediaservice.dto.trailer.TrailerRequest;
import org.example.mediaservice.dto.trailer.TrailerResponse;
import org.example.mediaservice.model.BoxOffice;
import org.example.mediaservice.model.MediaImage;
import org.example.mediaservice.model.Trailer;
import org.example.mediaservice.model.enums.ImageType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MediaMapper {

    default MediaImage fromUrlRequest(UUID titleId, ImageRequest req) {
        return MediaImage.builder()
                .titleId(titleId)                    // ✅ UUID scalar
                .imageType(req.imageType())
                .url(req.url())
                .width(req.width())
                .height(req.height())
                .isPrimary(req.isPrimary())
                .build();
    }

    default MediaImage fromPersonUrlRequest(UUID personId, ImageRequest req) {
        return MediaImage.builder()
                .personId(personId)                  // ✅ UUID scalar — no PersonRepository needed
                .imageType(req.imageType())
                .url(req.url())
                .width(req.width())
                .height(req.height())
                .isPrimary(req.isPrimary())
                .build();
    }

    default MediaImage fromTitledUpload(UUID titleId, UUID uploadedBy, String url, ImageType type) {
        return MediaImage.builder()
                .titleId(titleId)
                .imageType(type)
                .url(url)
                .uploadedBy(uploadedBy)
                .isPrimary(type == ImageType.POSTER)
                .build();
    }

    default MediaImage fromPersonUpload(UUID personId, UUID uploadedBy, String url, ImageType type) {
        return MediaImage.builder()
                .personId(personId)
                .imageType(type)
                .url(url)
                .uploadedBy(uploadedBy)
                .isPrimary(type == ImageType.PROFILE)
                .build();
    }

    default Trailer toEntity(UUID titleId, TrailerRequest req) {
        return Trailer.builder()
                .titleId(titleId)                    // ✅ UUID scalar
                .name(req.name())
                .trailerType(req.trailerType())
                .youtubeKey(req.youtubeKey())
                .durationSecs(req.durationSecs())
                .language(req.language())
                .publishedAt(req.publishedAt())
                .build();
    }

    default BoxOffice create(UUID titleId) {
        return BoxOffice.builder()
                .titleId(titleId)                    // ✅ UUID scalar only
                .build();
    }

    @Mapping(target = "id",      ignore = true)
    @Mapping(target = "titleId", ignore = true)
    void updateFromRequest(BoxOfficeDTO req, @MappingTarget BoxOffice boxOffice);

    ImageResponse toImageResponse(MediaImage image);

    TrailerResponse toTrailerResponse(Trailer trailer);

    BoxOfficeDTO toBoxOfficeResponse(BoxOffice boxOffice);
}

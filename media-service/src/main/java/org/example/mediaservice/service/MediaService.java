package org.example.mediaservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mediaservice.dto.image.ImageRequest;
import org.example.mediaservice.dto.image.ImageResponse;
import org.example.mediaservice.dto.trailer.TrailerRequest;
import org.example.mediaservice.dto.trailer.TrailerResponse;
import org.example.sharedmodule.general_exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import org.example.mediaservice.mapper.MediaMapper;
import org.example.mediaservice.model.MediaImage;
import org.example.mediaservice.model.enums.ImageType;
import org.example.mediaservice.repository.MediaImageRepository;
import org.example.mediaservice.repository.TrailerRepository;
import org.example.mediaservice.service.StorageService.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

    private final StorageService storageService;
    private final MediaImageRepository imageRepository;
    private final TrailerRepository   trailerRepository;
    private final MediaMapper mediaMapper;

    // ────────────────────────────────────────────────
    //  IMAGES — TITLE
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ImageResponse> getTitleImages(UUID titleId) {
        return imageRepository.findByTitleId(titleId)
                .stream()
                .map(mediaMapper::toImageResponse)
                .toList();
    }

    /**
     * Simple URL-based image add (no file upload) — for admin use.
     * Use uploadTitleImage() for multipart file uploads.
     */
    @Transactional
    public ImageResponse addTitleImage(UUID titleId, ImageRequest req) {
        // TODO is title exist
        if (Boolean.TRUE.equals(req.isPrimary())) {
            demoteExistingPrimary(titleId, null, req.imageType());
        }
        return mediaMapper.toImageResponse(imageRepository.save(mediaMapper.fromUrlRequest(titleId, req)));
    }


    /**
     * Multipart file upload — validates type/size, uploads to S3 or other storage,
     * demotes existing primary, persists MediaImage.
     *
     * NOTE: titleRepo.updatePosterUrl() is removed — this service doesn't own
     * the Title entity. Publish a MediaUploadedEvent (or call title-service
     * via Feign) to keep title.posterUrl in sync when needed.
     */
    @Transactional
    public ImageResponse uploadTitleImage(
            UUID titleId, UUID uploadedBy, MultipartFile file, ImageType type) {

        validateImageFile(file);

        String key = buildStorageKey("titles", titleId, type, file.getContentType());
        String url = uploadToStorage(key, file);

        try {
            // Demote existing primary of same type before persisting the new one
            if (type == ImageType.POSTER) {
                demoteExistingPrimary(titleId, null, type);
            }

            // TODO make event to Update title poster_url if primary poster
            return mediaMapper.toImageResponse(
                    imageRepository.save(mediaMapper.fromTitledUpload(titleId, uploadedBy, url, type)));
        } catch (RuntimeException e) {
            deleteUploadedObject(key);
            throw e;
        }
    }

    // ────────────────────────────────────────────────
    //  IMAGES — PERSON
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ImageResponse> getPersonImages(UUID personId) {
        return imageRepository.findByPersonId(personId)
                .stream()
                .map(mediaMapper::toImageResponse)
                .toList();
    }

    @Transactional
    public ImageResponse addPersonImage(UUID personId, ImageRequest req) {
        if (Boolean.TRUE.equals(req.isPrimary())) {
            demoteExistingPrimary(null, personId, req.imageType());
        }
        return mediaMapper.toImageResponse(imageRepository.save(mediaMapper.fromPersonUrlRequest(personId, req)));
    }

    @Transactional
    public ImageResponse uploadPersonImage(
            UUID personId, UUID uploadedBy, MultipartFile file, ImageType type) {

        validateImageFile(file);
        String key = buildStorageKey("people", personId, type, file.getContentType());
        String url = uploadToStorage(key, file);

        try {
            // Demote existing primary of same type before persisting the new one
            if (type == ImageType.PROFILE) {
                demoteExistingPrimary(null, personId, type);
            }

            return mediaMapper.toImageResponse(
                    imageRepository.save(mediaMapper.fromPersonUpload(personId, uploadedBy, url, type)));
        } catch (RuntimeException e) {
            deleteUploadedObject(key);
            throw e;
        }
    }

    // ────────────────────────────────────────────────
    //  TRAILERS
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TrailerResponse> getTrailers(UUID titleId) {
        return trailerRepository.findByTitleId(titleId)
                .stream()
                .map(mediaMapper::toTrailerResponse)
                .toList();
    }

    @Transactional
    public TrailerResponse addTrailer(UUID titleId, TrailerRequest req) {
        // TODO check if the title exist
        return mediaMapper.toTrailerResponse(trailerRepository.save(mediaMapper.toEntity(titleId, req)));
    }


    // ────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ────────────────────────────────────────────────

    private void validateImageFile(MultipartFile file) {
        Set<String> allowed = Set.of("image/jpeg", "image/png", "image/webp");
        if (!allowed.contains(file.getContentType()))
            throw new BusinessException("Invalid image type: " + file.getContentType(), HttpStatus.UNPROCESSABLE_CONTENT);
        if (file.getSize() > 10L * 1024 * 1024)
            throw new BusinessException("Image must be under 10 MB", HttpStatus.UNPROCESSABLE_CONTENT);
    }

    private String buildStorageKey(String domain, UUID entityId, ImageType type, String contentType) {
        String ext = switch (contentType) {
            case "image/png"  -> ".png";
            case "image/webp" -> ".webp";
            default           -> ".jpg";
        };
        return "media/" + domain + "/" + entityId + "/"
                + type.name().toLowerCase() + "/" + UUID.randomUUID() + ext;
    }

    private String uploadToStorage(String key, MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            storageService.Upload(key, stream, file.getContentType(), file.getSize());
            return storageService.getUrl(key);
        } catch (IOException e) {
            throw new BusinessException("Upload failed: " + e.getMessage(), HttpStatus.UNPROCESSABLE_CONTENT);
        }
    }

    private void demoteExistingPrimary(UUID titleId, UUID personId, ImageType type) {
        ImageType primaryType = switch (type) {
            case POSTER, PROFILE -> type;
            default -> null;
        };
        if (primaryType == null) return;

        List<MediaImage> existing = titleId != null
                ? imageRepository.findByTitleIdAndImageType(titleId, primaryType)
                : imageRepository.findByPersonIdAndImageType(personId, primaryType);

        existing.stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .forEach(img -> {
                    img.setIsPrimary(false);
                    imageRepository.save(img);
                });
    }

    private void deleteUploadedObject(String key) {
        try {
            storageService.Delete(key);
        } catch (IOException e) {
            log.warn("Failed to delete orphaned storage object {}: {}", key, e.getMessage());
        }
    }

}

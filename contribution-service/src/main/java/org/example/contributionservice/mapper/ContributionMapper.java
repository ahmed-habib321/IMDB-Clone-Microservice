package org.example.contributionservice.mapper;

import org.example.contributionservice.dto.GoofRequest;
import org.example.contributionservice.dto.GoofResponse;
import org.example.contributionservice.dto.QuoteRequest;
import org.example.contributionservice.dto.QuoteResponse;
import org.example.contributionservice.dto.TriviaRequest;
import org.example.contributionservice.dto.TriviaResponse;
import org.example.contributionservice.model.Goof;
import org.example.contributionservice.model.Quote;
import org.example.contributionservice.model.Trivia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ContributionMapper {

    default Trivia toEntity(UUID titleId, UUID userId, TriviaRequest req) {
        return Trivia.builder()
                .titleId(titleId)          // ✅ UUID scalar — no TitleRepository
                .body(req.body())
                .isSpoiler(req.isSpoiler())
                .contributedBy(userId)
                .isApproved(false)
                .helpfulCount(0)
                .build();
    }

    default Quote toEntity(UUID titleId, UUID userId, QuoteRequest req) {
        return Quote.builder()
                .titleId(titleId)          // ✅ UUID scalar — no TitleRepository
                .body(req.text())          // body holds the quote text
                .spokenBy(req.spokenBy())
                .contributedBy(userId)
                .isApproved(false)
                .helpfulCount(0)
                .build();
    }

    default Goof toEntity(UUID titleId, UUID userId, GoofRequest req) {
        return Goof.builder()
                .titleId(titleId)          // ✅ UUID scalar — no TitleRepository
                .body(req.body())
                .isSpoiler(req.isSpoiler())
                .goofType(req.goofType())
                .contributedBy(userId)
                .isApproved(false)
                .helpfulCount(0)
                .build();
    }

    // All Trivia fields (id, body, isSpoiler, isApproved, helpfulCount, createdAt)
    // are inherited from Contribution and match TriviaResponse 1:1
    TriviaResponse toTriviaResponse(Trivia trivia);

    // body (the quote text) and spokenBy come from Quote/Contribution
    @Mapping(target = "text", source = "body")
    QuoteResponse toQuoteResponse(Quote quote);

    // body, goofType, isSpoiler, helpfulCount from Goof/Contribution
    GoofResponse toGoofResponse(Goof goof);
}

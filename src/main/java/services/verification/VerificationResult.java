package services.verification;

import entities.VerificationStatus;

public record VerificationResult(
        VerificationStatus status,
        double score,
        String extractedCin,
        boolean keywordFellahFound,
        String reason
) {
}

package validators;

import entities.CollabApplication;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CollabApplicationValidatorTest {

    @Test
    void testValidateRequestId_Valid() {
        assertDoesNotThrow(() -> CollabApplicationValidator.validateRequestId(1L));
    }

    @Test
    void testValidateRequestId_Zero() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabApplicationValidator.validateRequestId(0L));
        assertEquals("L'ID de la demande est invalide", exception.getMessage());
    }

    @Test
    void testValidateRequestId_Negative() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabApplicationValidator.validateRequestId(-1L));
        assertEquals("L'ID de la demande est invalide", exception.getMessage());
    }

    @Test
    void testValidateCandidateId_Valid() {
        assertDoesNotThrow(() -> CollabApplicationValidator.validateCandidateId(1L));
    }

    @Test
    void testValidateCandidateId_Zero() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabApplicationValidator.validateCandidateId(0L));
        assertEquals("L'ID du candidat est invalide", exception.getMessage());
    }

    @Test
    void testValidateCandidateId_Negative() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabApplicationValidator.validateCandidateId(-1L));
        assertEquals("L'ID du candidat est invalide", exception.getMessage());
    }

    @Test
    void testValidateMessage_Null() {
        assertDoesNotThrow(() -> CollabApplicationValidator.validateMessage(null));
    }

    @Test
    void testValidateMessage_Empty() {
        assertDoesNotThrow(() -> CollabApplicationValidator.validateMessage("   "));
    }

    @Test
    void testValidateMessage_Valid() {
        assertDoesNotThrow(() -> CollabApplicationValidator.validateMessage("Valid message with enough characters"));
    }

    @Test
    void testValidateMessage_TooShort() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabApplicationValidator.validateMessage("Short"));
        assertEquals("Le message doit contenir au moins 10 caractères", exception.getMessage());
    }

    @Test
    void testValidateMessage_TooLong() {
        String longMessage = "A".repeat(256);
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabApplicationValidator.validateMessage(longMessage));
        assertEquals("Le message ne peut pas dépasser 255 caractères", exception.getMessage());
    }

    @Test
    void testValidate_CompleteValidApplication() {
        CollabApplication application = new CollabApplication();
        application.setRequestId(1L);
        application.setCandidateId(2L);
        application.setMessage("This is a valid application message");
        
        assertTrue(CollabApplicationValidator.validate(application));
    }

    @Test
    void testValidate_ValidWithoutMessage() {
        CollabApplication application = new CollabApplication();
        application.setRequestId(1L);
        application.setCandidateId(2L);
        application.setMessage(null);
        
        assertTrue(CollabApplicationValidator.validate(application));
    }

    @Test
    void testValidate_InvalidRequestId() {
        CollabApplication application = new CollabApplication();
        application.setRequestId(0L); // Invalid
        application.setCandidateId(2L);
        application.setMessage("Valid message");
        
        assertThrows(IllegalArgumentException.class, 
            () -> CollabApplicationValidator.validate(application));
    }

    @Test
    void testValidate_InvalidCandidateId() {
        CollabApplication application = new CollabApplication();
        application.setRequestId(1L);
        application.setCandidateId(-1L); // Invalid
        application.setMessage("Valid message");
        
        assertThrows(IllegalArgumentException.class, 
            () -> CollabApplicationValidator.validate(application));
    }
}

package validators;

import entities.CollabRequest;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class CollabRequestValidatorTest {

    @Test
    void testValidateTitle_Valid() {
        assertDoesNotThrow(() -> CollabRequestValidator.validateTitle("Valid Title"));
    }

    @Test
    void testValidateTitle_Null() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateTitle(null));
        assertEquals("Le titre est obligatoire", exception.getMessage());
    }

    @Test
    void testValidateTitle_Empty() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateTitle("   "));
        assertEquals("Le titre est obligatoire", exception.getMessage());
    }

    @Test
    void testValidateTitle_TooShort() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateTitle("Test"));
        assertEquals("Le titre doit contenir au moins 5 caractères", exception.getMessage());
    }

    @Test
    void testValidateTitle_TooLong() {
        String longTitle = "A".repeat(151);
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateTitle(longTitle));
        assertEquals("Le titre ne peut pas dépasser 150 caractères", exception.getMessage());
    }

    @Test
    void testValidateDescription_Valid() {
        assertDoesNotThrow(() -> CollabRequestValidator.validateDescription("Valid description"));
    }

    @Test
    void testValidateDescription_Null() {
        assertDoesNotThrow(() -> CollabRequestValidator.validateDescription(null));
    }

    @Test
    void testValidateDescription_TooLong() {
        String longDesc = "A".repeat(501);
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateDescription(longDesc));
        assertEquals("La description ne peut pas dépasser 500 caractères", exception.getMessage());
    }

    @Test
    void testValidateDates_Valid() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(10);
        assertDoesNotThrow(() -> CollabRequestValidator.validateDates(startDate, endDate));
    }

    @Test
    void testValidateDates_NullStartDate() {
        LocalDate endDate = LocalDate.now().plusDays(10);
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateDates(null, endDate));
        assertEquals("La date de début est obligatoire", exception.getMessage());
    }

    @Test
    void testValidateDates_NullEndDate() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateDates(startDate, null));
        assertEquals("La date de fin est obligatoire", exception.getMessage());
    }

    @Test
    void testValidateDates_PastStartDate() {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(10);
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateDates(startDate, endDate));
        assertEquals("La date de début doit être dans le futur", exception.getMessage());
    }

    @Test
    void testValidateDates_TodayStartDate() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(10);
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateDates(startDate, endDate));
        assertEquals("La date de début doit être dans le futur", exception.getMessage());
    }

    @Test
    void testValidateDates_EndBeforeStart() {
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(5);
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateDates(startDate, endDate));
        assertEquals("La date de fin doit être après la date de début", exception.getMessage());
    }

    @Test
    void testValidateDates_EndEqualsStart() {
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(10);
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateDates(startDate, endDate));
        assertEquals("La date de fin doit être après la date de début", exception.getMessage());
    }

    @Test
    void testValidateNeededPeople_Valid() {
        assertDoesNotThrow(() -> CollabRequestValidator.validateNeededPeople(5));
    }

    @Test
    void testValidateNeededPeople_Zero() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateNeededPeople(0));
        assertEquals("Le nombre de personnes doit être au moins 1", exception.getMessage());
    }

    @Test
    void testValidateNeededPeople_Negative() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateNeededPeople(-1));
        assertEquals("Le nombre de personnes doit être au moins 1", exception.getMessage());
    }

    @Test
    void testValidateNeededPeople_TooMany() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateNeededPeople(51));
        assertEquals("Le nombre de personnes ne peut pas dépasser 50", exception.getMessage());
    }

    @Test
    void testValidateRequesterId_Valid() {
        assertDoesNotThrow(() -> CollabRequestValidator.validateRequesterId(1L));
    }

    @Test
    void testValidateRequesterId_Zero() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateRequesterId(0L));
        assertEquals("L'ID du demandeur est invalide", exception.getMessage());
    }

    @Test
    void testValidateRequesterId_Negative() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validateRequesterId(-1L));
        assertEquals("L'ID du demandeur est invalide", exception.getMessage());
    }

    @Test
    void testValidate_CompleteValidRequest() {
        CollabRequest request = new CollabRequest();
        request.setRequesterId(1L);
        request.setTitle("Valid collaboration title");
        request.setDescription("A valid description");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(10));
        request.setNeededPeople(5);
        
        assertTrue(CollabRequestValidator.validate(request));
    }

    @Test
    void testValidate_InvalidRequest() {
        CollabRequest request = new CollabRequest();
        request.setRequesterId(0L); // Invalid
        request.setTitle("Test");
        request.setDescription("Description");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(10));
        request.setNeededPeople(5);
        
        assertThrows(IllegalArgumentException.class, 
            () -> CollabRequestValidator.validate(request));
    }
}

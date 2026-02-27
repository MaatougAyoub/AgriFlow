package entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

/**
 * Tests unitaires pour l'entité CollabRequest
 */
class CollabRequestTest {
    
    private CollabRequest request;
    
    @BeforeEach
    void setUp() {
        request = new CollabRequest();
    }
    
    @Test
    @DisplayName("Test constructeur avec paramètres")
    void testConstructorWithParameters() {
        // Arrange
        long requesterId = 2;
        String title = "Récolte olives";
        String description = "Besoin de 2 personnes";
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(2);
        int neededPeople = 2;
        String status = "PENDING";
        
        // Act
        CollabRequest req = new CollabRequest(requesterId, title, description, 
                                               startDate, endDate, neededPeople, status);
        
        // Assert
        assertEquals(requesterId, req.getRequesterId());
        assertEquals(title, req.getTitle());
        assertEquals(description, req.getDescription());
        assertEquals(startDate, req.getStartDate());
        assertEquals(endDate, req.getEndDate());
        assertEquals(neededPeople, req.getNeededPeople());
        assertEquals(status, req.getStatus());
    }
    
    @Test
    @DisplayName("Test setters et getters")
    void testSettersAndGetters() {
        // Arrange
        LocalDate testStartDate = LocalDate.now().plusDays(5);
        LocalDate testEndDate = LocalDate.now().plusDays(10);
        
        // Act
        request.setId(1L);
        request.setRequesterId(5);
        request.setTitle("Test Title");
        request.setDescription("Test Description");
        request.setStartDate(testStartDate);
        request.setEndDate(testEndDate);
        request.setNeededPeople(3);
        request.setStatus("APPROVED");
        
        // Assert
        assertEquals(1L, request.getId());
        assertEquals(5, request.getRequesterId());
        assertEquals("Test Title", request.getTitle());
        assertEquals("Test Description", request.getDescription());
        assertEquals(testStartDate, request.getStartDate());
        assertEquals(testEndDate, request.getEndDate());
        assertEquals(3, request.getNeededPeople());
        assertEquals("APPROVED", request.getStatus());
    }
    
    @Test
    @DisplayName("Test toString non null")
    void testToString() {
        // Arrange
        request.setId(1L);
        request.setTitle("Récolte olives");
        request.setNeededPeople(2);
        
        // Act
        String result = request.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("1"));
        assertTrue(result.contains("Récolte olives"));
        assertTrue(result.contains("2"));
    }
}

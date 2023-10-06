import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceTest {


  private TicketService ticketService;

  @BeforeEach
  public void setup() {
    ticketService = new TicketServiceImpl();
  }

  @Test
  @DisplayName("Given an invalid account ID then throw an exception")
  public void givenInvalidAccountIdThrowException() {

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> ticketService.purchaseTickets(0L));

    assertEquals(exception.getMessage(), "Invalid Account ID", "should return invalid account ID");
  }

}

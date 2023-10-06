import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

  @Mock
  private TicketPaymentService paymentService;

  @Mock
  private SeatReservationService reservationService;

  private TicketService ticketService;

  @BeforeEach
  public void setup() {
    ticketService = new TicketServiceImpl(paymentService, reservationService);
  }

  @ParameterizedTest
  @CsvSource({"0", "-1"})
  @DisplayName("Given an invalid account ID then throw an exception")
  public void givenInvalidAccountIdThrowException(long input) {

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> ticketService.purchaseTickets(input));

    assertEquals(exception.getMessage(), "Invalid Account ID", "should return invalid account ID");
  }

  @Test
  @DisplayName("Given a valid accountID and an empty ticket,then no payment and reservation is made")
  public void givenEmptyTicket() {

    ticketService.purchaseTickets(1L);

    verify(paymentService, never()).makePayment(anyLong(), anyInt());
    verify(reservationService, never()).reserveSeat(anyLong(), anyInt());
  }

  @Test
  @DisplayName("Given a valid accountID and an list of tickets with no adult, then throw exception is no adult ticket present")
  public void givenListOfTicketWithNoAdult() {

    TicketTypeRequest childTicket = new TicketTypeRequest(Type.CHILD, 4);
    TicketTypeRequest infantTicket = new TicketTypeRequest(Type.CHILD, 1);

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> ticketService.purchaseTickets(1L, childTicket, infantTicket));

    assertEquals(exception.getMessage(), "Adult ticket is required",
        "should return Adult ticket is required");
  }

  @Test
  @DisplayName("Given a valid accountID and an list of tickets, then throw exception if more than the max(20)")
  public void checkTicketDoesExceed20() {

    TicketTypeRequest adultTicket = new TicketTypeRequest(Type.ADULT, 12);
    TicketTypeRequest childTicket = new TicketTypeRequest(Type.CHILD, 6);
    TicketTypeRequest infantTicket = new TicketTypeRequest(Type.CHILD, 3);

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> ticketService.purchaseTickets(1L, adultTicket, childTicket, infantTicket));

    assertEquals(exception.getMessage(), "Maximum ticket exceeded",
        "should return Maximum ticket exceeded");
  }

  @Test
  @DisplayName("Given a valid accountId and a list of valid tickets, then compute total amount to pay")
  public void givenTicketsCalculateAmount() {

    TicketTypeRequest adultTicket = new TicketTypeRequest(Type.ADULT, 2);
    TicketTypeRequest childTicket = new TicketTypeRequest(Type.CHILD, 2);
    TicketTypeRequest infantTicket = new TicketTypeRequest(Type.INFANT, 3);

    ticketService.purchaseTickets(1L, adultTicket, childTicket, infantTicket);

    verify(paymentService, times(1)).makePayment(eq(1L), eq(60));
  }

  @Test
  @DisplayName("Given a valid accountId and a list of valid tickets, then compute total seat to reserve")
  public void computeSeatToReserve() {

    TicketTypeRequest adultTicket = new TicketTypeRequest(Type.ADULT, 4);
    TicketTypeRequest childTicket = new TicketTypeRequest(Type.CHILD, 2);
    TicketTypeRequest infantTicket = new TicketTypeRequest(Type.INFANT, 3);

    ticketService.purchaseTickets(1L, adultTicket, childTicket, infantTicket);

    verify(reservationService, times(1)).reserveSeat(eq(1L), eq(6));
  }
}

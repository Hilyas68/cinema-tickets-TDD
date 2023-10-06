package uk.gov.dwp.uc.pairtest;

import java.util.Arrays;
import java.util.Optional;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketDetailDto;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

  public static final int MIN_ACCOUNT_ID = 1;
  public static final String INVALID_ACCOUNT_ID_MESSAGE = "Invalid Account ID";
  public static final int MAX_TICKET = 20;
  public static final String MAXIMUM_TICKET_EXCEEDED_MESSAGE = "Maximum ticket exceeded";
  public static final String ADULT_TICKET_IS_REQUIRED_MESSAGE = "Adult ticket is required";

  private final TicketPaymentService paymentService;
  private final SeatReservationService reservationService;

  /**
   * Should only have private methods other than the one below.
   */

  public TicketServiceImpl(TicketPaymentService paymentService,
      SeatReservationService reservationService) {
    this.paymentService = paymentService;
    this.reservationService = reservationService;
  }

  @Override
  public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
      throws InvalidPurchaseException {

    if (accountId < MIN_ACCOUNT_ID) {
      throw new InvalidPurchaseException(INVALID_ACCOUNT_ID_MESSAGE);
    }

    if (ticketTypeRequests.length > 0) {

      TicketDetailDto ticketDetails = getTotalTickets(ticketTypeRequests);
      if (ticketDetails.getTotalTickets() > MAX_TICKET) {
        throw new InvalidPurchaseException(MAXIMUM_TICKET_EXCEEDED_MESSAGE);
      }

      int totalTicketsPrice = getTotalTicketsPrice(ticketDetails);
      int totalSeatToReserve = getTotalSeatToReserve(ticketDetails);

      paymentService.makePayment(accountId, totalTicketsPrice);
      reservationService.reserveSeat(accountId, totalSeatToReserve);
    }
  }

  private static int getTotalSeatToReserve(TicketDetailDto ticketDetails) {
    return ticketDetails.getNumberOfAdults() + ticketDetails.getNumChildren();
  }

  private static int getTotalTicketsPrice(final TicketDetailDto ticketDetails) {
    return ticketDetails.getNumberOfAdults() * 20 + ticketDetails.getNumChildren() * 10;
  }

  private static TicketDetailDto getTotalTickets(final TicketTypeRequest[] ticketTypeRequests) {
    int numberOfAdults = 0;
    int numChildren = 0;
    int numberOfInfants = 0;

    for (TicketTypeRequest request : ticketTypeRequests) {
      switch (request.getTicketType()) {
        case ADULT -> numberOfAdults += request.getNoOfTickets();
        case CHILD -> numChildren += request.getNoOfTickets();
        case INFANT -> numberOfInfants += request.getNoOfTickets();
      }
    }

    if (numberOfAdults == 0) {
      throw new InvalidPurchaseException(ADULT_TICKET_IS_REQUIRED_MESSAGE);
    }

    return new TicketDetailDto(numberOfAdults, numChildren, numberOfInfants,
        numberOfAdults + numChildren + numberOfInfants);
  }

}

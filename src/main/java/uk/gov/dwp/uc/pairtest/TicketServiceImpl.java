package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

  public static final int MIN_ACCOUNT_ID = 1;
  public static final String INVALID_ACCOUNT_ID_MESSAGE = "Invalid Account ID";

  /**
   * Should only have private methods other than the one below.
   */

  @Override
  public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
      throws InvalidPurchaseException {

    if (accountId < MIN_ACCOUNT_ID) {
      throw new InvalidPurchaseException(INVALID_ACCOUNT_ID_MESSAGE);
    }
  }

}

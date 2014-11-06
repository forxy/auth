package auth.service;

import auth.api.v1.pojo.Token;
import common.pojo.EntityPage;
import common.pojo.SortDirection;

/**
 * Entry point into token service business logic
 */
public interface ITokenService {

    Iterable<Token> getAllTokens();

    EntityPage<Token> getTokens(final Integer page, final Integer size, final SortDirection sortDirection,
                                final String sortedBy, final Token filter);

    Token getToken(final String tokenID);

    void updateToken(final Token token);

    void createToken(final Token token);

    void deleteToken(final String tokenID);
}

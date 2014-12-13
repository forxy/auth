package auth.service

import auth.api.v1.Token
import auth.db.dao.ITokenDAO
import auth.exceptions.AuthEvent
import common.exceptions.ServiceException
import common.api.EntityPage
import common.api.SortDirection
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * Implementation class for AuthService business logic
 */
class TokenService implements ITokenService {

    static final int DEFAULT_PAGE_SIZE = 10

    ITokenDAO tokenDAO

    List<Token> getAllTokens() {
        return tokenDAO.findAll().collect { it }
    }

    @Override
    EntityPage<Token> getTokens(final Integer page, final Integer size, final SortDirection sortDirection,
                                final String sortedBy, final Token filter) {
        if (page >= 1) {
            int pageSize = size == null ? DEFAULT_PAGE_SIZE : size
            PageRequest pageRequest
            if (sortDirection != null && sortedBy != null) {
                Sort.Direction dir = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC
                pageRequest = new PageRequest(page - 1, pageSize, dir, sortedBy)
            } else {
                pageRequest = new PageRequest(page - 1, pageSize)
            }
            final Page<Token> p = tokenDAO.findAll(pageRequest, filter)
            return new EntityPage<>(p.getContent(), p.getSize(), p.getNumber(), p.getTotalElements())
        } else {
            throw new ServiceException(AuthEvent.InvalidPageNumber, page)
        }
    }

    @Override
    Token getToken(final String email) {
        Token token = tokenDAO.findOne(email)
        if (token == null) {
            throw new ServiceException(AuthEvent.TokenNotFound, email)
        }
        return token
    }

    @Override
    void updateToken(final Token token) {
        if (tokenDAO.exists(token.getTokenKey())) {
            tokenDAO.save(token)
        } else {
            throw new ServiceException(AuthEvent.TokenNotFound, token.getTokenKey())
        }
    }

    @Override
    void createToken(final Token token) {
        if (!tokenDAO.exists(token.getTokenKey())) {
            tokenDAO.save(token)
        } else {
            throw new ServiceException(AuthEvent.TokenAlreadyExists, token.getTokenKey())
        }
    }

    @Override
    void deleteToken(final String email) {
        if (tokenDAO.exists(email)) {
            tokenDAO.delete(email)
        } else {
            throw new ServiceException(AuthEvent.TokenNotFound, email)
        }
    }

    void setTokenDAO(final ITokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO
    }
}

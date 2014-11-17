package auth.db.dao

import auth.api.v1.pojo.Token
import common.status.ISystemStatusComponent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.PagingAndSortingRepository

/**
 * Data Access Object for Forxy database to manipulate tokens.
 */
interface ITokenDAO extends ISystemStatusComponent {

    Page<Token> findAll(final Pageable pageable, final Token filter)

    Iterable<Token> findAll(final Sort sort)

    Page<Token> findAll(final Pageable pageable)

    Token save(final Token token)

    Token findOne(final String s)

    boolean exists(final String s)

    Iterable<Token> findAll()

    long count()

    void delete(final String s)

    void delete(final Token token)
}


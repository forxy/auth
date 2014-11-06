package auth.db.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import auth.api.v1.pojo.Token;
import common.status.ISystemStatusComponent;

/**
 * Data Access Object for Forxy database to manipulate tokens.
 */
public interface ITokenDAO extends PagingAndSortingRepository<Token, String>, ISystemStatusComponent {

    Page<Token> findAll(final Pageable pageable, final Token filter);
}


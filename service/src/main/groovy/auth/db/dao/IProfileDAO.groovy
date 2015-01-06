package auth.db.dao

import auth.api.v1.Profile
import common.status.ISystemStatusComponent

/**
 * Data Access Object for Profile database to manipulate Profiles.
 */
interface IProfileDAO extends ISystemStatusComponent {

    Profile save(final Profile user)

    Profile getProfile(final String s)
}


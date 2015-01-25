db.user.insert({
    "_id": "askadias@gmail.com",
    "password": "eeb5d0f736ab20b7ef18f00f20c5b62de44202f3ab153ce132dc503c381f8e0721ac73a5aa26ae73",
    "login": "kast",
    "first_name": "Kast",
    "last_name": "Askadias",
    "gender": "MALE",
    "groups": [
        "users",
        "auth_admins"
    ],
    "update_date": ISODate("2014-07-17T21:00:00.000Z"),
    "create_date": ISODate("2014-07-17T21:00:00.000Z")
});

db.user.insert({
    "_id": "askadias@mail.ru",
    "password": "eeb5d0f736ab20b7ef18f00f20c5b62de44202f3ab153ce132dc503c381f8e0721ac73a5aa26ae73",
    "login": "askadias",
    "first_name": "Kast",
    "last_name": "Askadias",
    "gender": "MALE",
    "groups": [
        "users"
    ],
    "update_date": ISODate("2014-12-16T14:58:04.978Z"),
    "create_date": ISODate("2014-06-29T21:00:00.000Z")
});

db.user.insert({
    "_id": "admin@forxy.ru",
    "password": "eeb5d0f736ab20b7ef18f00f20c5b62de44202f3ab153ce132dc503c381f8e0721ac73a5aa26ae73",
    "login": "admin",
    "first_name": "admin",
    "last_name": "admin",
    "gender": "MALE",
    "groups": [
        "users",
        "auth_admins"
    ],
    "update_date": ISODate("2014-08-18T21:00:00.000Z"),
    "create_date": ISODate("2014-08-18T21:00:00.000Z")
});

db.user.ensureIndex( { email: 1} );

db.user_group.insert({
    "_id": "users",
    "name": "Users",
    "description": "Group of common users",
    "members": [
        "admin@forxy.ru",
        "askadias@gmail.com",
        "askadias@mail.ru"
    ],
    "update_date": ISODate("2014-07-17T21:00:00.000Z"),
    "create_date": ISODate("2014-07-17T21:00:00.000Z")
});

db.user_group.insert({
    "_id": "auth_admins",
    "name": "Auth Admins",
    "description": "Auth Service administrators",
    "members": [
        "admin@forxy.ru",
        "askadias@gmail.com"
    ],
    "update_date": ISODate("2014-07-17T21:00:00.000Z"),
    "create_date": ISODate("2014-07-17T21:00:00.000Z")
});

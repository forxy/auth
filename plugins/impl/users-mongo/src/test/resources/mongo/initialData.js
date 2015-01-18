db.user.insert({
    "_id": "kast",
    "password": "ab518f2267abc8bac15c1af41fe0472a62bf36de27ebca8e62b5fad23e6edd6caec6058cd6e8a186",
    "login": "askadias@gmail.com",
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
    "_id": "askadias",
    "password": "ab518f2267abc8bac15c1af41fe0472a62bf36de27ebca8e62b5fad23e6edd6caec6058cd6e8a186",
    "email": "askadias@mail.ru",
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
    "_id": "admin",
    "password": "00e241282558cde7e030eb7c2b579b3f3e6200978fb884d94f2128b989a5cbc920110a8b55ed27e9",
    "email": "admin@admin.com",
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
        "admin",
        "kast",
        "askadias"
    ],
    "update_date": ISODate("2014-07-17T21:00:00.000Z"),
    "create_date": ISODate("2014-07-17T21:00:00.000Z")
});

db.user_group.insert({
    "_id": "auth_admins",
    "name": "Auth Admins",
    "description": "Auth Service administrators",
    "members": [
        "admin",
        "kast"
    ],
    "update_date": ISODate("2014-07-17T21:00:00.000Z"),
    "create_date": ISODate("2014-07-17T21:00:00.000Z")
});
db.client.insert({
    "_id" : "290a3fc6-b7c6-46be-85a8-fedd1c45a9f9",
    "email" : "290a3fc6-b7c6-46be-85a8-fedd1c45a9f9@forxy.ru",
    "password" : "a5a0ffe6d44ca442e604a9aef205831b7cf725caf1299bac1fe745d9886640eb68a5138fc1330fb5",
    "name" : "Auth Service",
    "description" : "Authentication / Authorization REST service",
    "web_uri" : "https://localhost:11080/auth/rest/v1/",
    "redirect_uris" : [],
    "scopes" : [
        "openid",
        "profile",
        "users_create",
        "users_update",
        "users_get",
        "users_delete",
        "clients_create",
        "clients_update",
        "clients_get",
        "clients_delete",
        "groups_create",
        "groups_update",
        "groups_get",
        "groups_delete",
        "tokens_get",
        "tokens_manage"
    ],
    "audiences" : [],
    "update_date" : ISODate("2014-08-21T10:25:33.474Z"),
    "create_date" : ISODate("2014-08-21T10:25:33.477Z")
});

db.client.insert({
    "_id" : "bcf901ea-3c87-4302-91cd-620ae4c80f9c",
    "email" : "bcf901ea-3c87-4302-91cd-620ae4c80f9c@forxy.ru",
    "password" : "828b7488eaefd5001f08a8bd1296fc37b347c8c92f1be435f360220c552dad34dbd9883613d6edb2",
    "name" : "Fraud Service",
    "description" : "Fraud REST service",
    "web_uri" : "https://localhost:12080/fraud/rest/v1/",
    "redirect_uris" : [],
    "scopes" : [
        "openid",
        "profile",
        "derogs_create",
        "derogs_update",
        "derogs_get",
        "derogs_delete",
        "velocity_config_create",
        "velocity_config_update",
        "velocity_config_get",
        "velocity_config_delete",
        "fraud_check"
    ],
    "audiences" : [],
    "update_date" : ISODate("2014-08-21T10:25:33.474Z"),
    "create_date" : ISODate("2014-08-21T10:25:33.477Z")
});

db.client.ensureIndex( { email: 1} );
db.client.ensureIndex( { name: 1} );